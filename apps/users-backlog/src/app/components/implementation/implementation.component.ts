import { Component, OnInit, OnDestroy, ViewChild, ElementRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { tap, first, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation.model';
import { ImplementationService } from 'src/app/services/implementation.service';
import { RecommendationForm } from 'src/app/models/forms/recommendation.form';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Recommendation } from 'src/app/models/recommendation.model';
import { ReplyForm } from 'src/app/models/forms/reply.form';
import { Reply } from 'src/app/models/reply.model';
import { InnovatorService } from 'src/app/services/innovator.service';
import { SessionStorageService } from 'src/app/services/session-storage.service';
import { ProductForm } from 'src/app/models/forms/product.form';
import { Product } from 'src/app/models/product.model';
import { IdeaAssociationForm } from 'src/app/models/forms/idea-association.form';
import { SearchService } from 'src/app/services/search.service';
import { Idea } from 'src/app/models/idea.model';

@Component({
  selector: 'app-implementation',
  templateUrl: './implementation.component.html',
  styleUrls: ['./implementation.component.css']
})
export class ImplementationComponent implements OnInit, OnDestroy {

  @ViewChild("newProduct") private newProductDropdown: ElementRef;

  implementation: Implementation;

  productForm: FormGroup;
  editProductForm: FormGroup;
  editedProduct: Product;

  ideaAssociationForm: FormGroup;
  ideaOptions: Idea[] = [];
  
  recommendationForm: FormGroup;
  editRecommendationForm: FormGroup;
  editedRecommendation: Recommendation<Implementation>;

  replyForm: FormGroup;
  editReplyForm: FormGroup;
  editedReply: Reply<Implementation>;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _implementationService: ImplementationService,
    private readonly _route: ActivatedRoute,
    private readonly _router: Router,
    private readonly _formBuilder: FormBuilder,
    public readonly innovatorService: InnovatorService,
    private readonly _sessionStorageService: SessionStorageService,
    private readonly _searchService: SearchService,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.productForm = this._formBuilder.group(new ProductForm());
    this.editProductForm = this._formBuilder.group(new ProductForm());

    this.ideaAssociationForm = this._formBuilder.group(new IdeaAssociationForm());
    this.ideaAssociationForm.controls.summary.valueChanges.pipe(
      tap(() => {
        if (this.ideaAssociationForm.controls.summary.value.length > 0) {
          this._searchService.searchForIdeas(this.ideaAssociationForm.controls.summary.value).pipe(
            first(),
            tap((ideas: Idea[]) => {
              this.ideaOptions = ideas.filter(idea => !this.implementation.ideas.map(i => i.id).includes(idea.id));
            }),
            catchError((error: any) => {
              console.error(error);
              this._snackBar.open('Failed to search for ideas.', 'Close');
              return of(null);
            }),
            takeUntil(this._destroyed$)
          ).subscribe();
        } else {
          this.ideaOptions = [];
        }
      }),
      catchError((error: any) => {
        console.error(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();

    this.recommendationForm = this._formBuilder.group(new RecommendationForm());
    this.editRecommendationForm = this._formBuilder.group(new RecommendationForm());

    this.replyForm = this._formBuilder.group(new ReplyForm());
    this.editReplyForm = this._formBuilder.group(new ReplyForm());

    this._route.data.pipe(
      first(),
      tap((data: {implementation: Implementation}) => {
        this.implementation = data.implementation;

        let parameters = this._sessionStorageService.retrieveData('postImplementationVote');
        if (parameters) {
          this.postVote(parameters.up);
        }
    
        parameters = this._sessionStorageService.retrieveData('postImplementationRecommendation');
        if (parameters) {
          this.recommendationForm.patchValue(parameters.recommendation);
          this.postRecommendation(this.recommendationForm);
        }
    
        parameters = this._sessionStorageService.retrieveData('postImplementationRecommendationVote');
        if (parameters) {
          const recommendation = this.implementation.recommendations.filter(r => r.id = parameters.recommendationId)[0];
          this.postRecommendationVote(recommendation, parameters.up);
        }
    
        parameters = this._sessionStorageService.retrieveData('postImplementationRecommendationReply');
        if (parameters) {
          this.replyForm.patchValue(parameters.reply);
          const recommendation = this.implementation.recommendations.filter(r => r.id = parameters.reply.recommendation.id)[0];
          this.postReply(this.replyForm, recommendation);
        }
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load implementation.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  public canEdit(): boolean {
    return this.canDelete() || this.implementation.innovator === undefined;
  }

  public editImplementation() {
    this._implementationService.implementationForEditing = this.implementation;
    this._router.navigate(['/edit-implementation']);
  }

  public canDelete(): boolean {
    return this.innovatorService.innovator$.value && this.implementation.innovator && this.innovatorService.innovator$.value.id === this.implementation.innovator.id;
  }

  public deleteImplementation() {
    this._implementationService.deleteImplementation(this.implementation).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          this._router.navigate(['/implementations']);
        } else {
          this._snackBar.open('Failed to delete implementation.', 'Close');
        }
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to delete implementation.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  public postProduct(productForm: FormGroup) {
    if (productForm.invalid) {
      productForm.markAllAsTouched()
    } else {
      const product = productForm.value as Product;
      product.implementation = this.implementation;
      this._implementationService.postProduct(product).pipe(
        first(),
        tap((updatedProduct: Product) => {
          const products = this.implementation.products.filter(product => product.id !== updatedProduct.id);
          products.push(updatedProduct);
          this.implementation.products = products;
          this.newProductDropdown.nativeElement.classList.remove('show');
        }),
        catchError((error: any) => {
          console.error(error);
          this._snackBar.open('Failed to post product.', 'Close');
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    }
  }

  public deleteProduct(product: Product): void {
    this._implementationService.deleteProduct(product).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const products = this.implementation.products.filter(p => p.id !== product.id);
          this.implementation.products = products;
        } else {
          this._snackBar.open('Failed to delete product.', 'Close');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to delete product.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public openProductUrl(url: string) {
    try {
      const parsedURL = new URL(url);
      window.open(parsedURL.href, '_blank');
    } catch (error) {
      console.error(error);
    }
  }

  public openProductEdit(product: Product) {
    this.editedProduct = product;
    this.editProductForm.patchValue(product);
  }

  public cancelEditProduct() {
    this.editedProduct = undefined;
  }

  public associateIdeaValid(): boolean {
    return this.ideaOptions.map(option => option.summary).includes(this.ideaAssociationForm.controls.summary.value);
  }

  public associateWithIdea() {
    const idea = this.ideaOptions.filter(idea => idea.summary === this.ideaAssociationForm.controls.summary.value)[0];
    this._implementationService.associateWithIdea(this.implementation, idea).pipe(
      first(),
      tap((added: boolean) => {
        if (added) {
          this.implementation.ideas.push(idea);
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to associate with idea.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public disassociateWithIdea(idea: Idea) {
    this._implementationService.disassociateWithIdea(this.implementation, idea).pipe(
      first(),
      tap((removed: boolean) => {
        if (removed) {
          this.implementation.ideas = this.implementation.ideas.filter(i => i.id !== idea.id);
        }      
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to disassociate with idea.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public postVote(up: boolean) {
    this._implementationService.postVote(this.implementation, up).pipe(
      first(),
      tap((votes: number) => {
        this.implementation.votes = votes;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to cast vote.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  public postRecommendation(recommendationForm: FormGroup): void {
    const recommendation = recommendationForm.value;
    if (recommendationForm.controls.message.value.length === 0) {
      recommendationForm.markAllAsTouched();
    } else {
      recommendation.parent = this.implementation;
      this._implementationService.postRecommendation(recommendation).pipe(
        first(),
        tap((returnedRecommendation: Recommendation<Implementation>) => {
          recommendationForm.reset();
          if (recommendation.id > 0) {
            const filteredRecommendations = this.implementation.recommendations.filter(r => r.id === recommendation.id);
            const index = this.implementation.recommendations.indexOf(filteredRecommendations[0]);
            this.implementation.recommendations.splice(index, 1, returnedRecommendation);
            this.editedRecommendation = undefined;
          } else {
            this.implementation.recommendations.push(returnedRecommendation);
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.error(e);
          this._snackBar.open('Failed to post recommendation.', 'Close');
          return of(null);
        })
      ).subscribe();
    }
  }

  public deleteRecommendation(recommendation: Recommendation<Implementation>): void {
    this._implementationService.deleteRecommendation(recommendation).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const recommendations = this.implementation.recommendations.filter(r => r.id !== recommendation.id);
          this.implementation.recommendations = recommendations;
        } else {
          this._snackBar.open('Failed to delete recommendation.', 'Close');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to delete recommendation.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public postRecommendationVote(recommendation: Recommendation<Implementation>, up: boolean) {
    this._implementationService.postRecommendationVote(recommendation, up).pipe(
      first(),
      tap((votes: number) => {
        recommendation.votes = votes;
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to cast vote.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public cancelEditRecommendtion() {
    this.editedRecommendation = undefined;
  }

  public openRecommendationEdit(recommendation: Recommendation<Implementation>) {
    this.editedRecommendation = recommendation;
    this.editRecommendationForm.patchValue(recommendation);
  }

  public postReply(replyForm: FormGroup, recommendation: Recommendation<Implementation>) {
    const reply = replyForm.value;
    if (!replyForm.controls.message.value) {
      replyForm.markAllAsTouched();
    } else {
      reply.recommendation = recommendation;
      this._implementationService.postRecommendationReply(reply).pipe(
        first(),
        tap((returnedReply: Reply<Implementation>) => {
          replyForm.reset();
          if (reply.id > 0) {
            const filteredReplies = recommendation.replies.filter(r => r.id === reply.id);
            const index = recommendation.replies.indexOf(filteredReplies[0]);
            recommendation.replies.splice(index, 1, returnedReply);
            this.editedRecommendation = undefined;
          } else {
            if (!recommendation.replies) {
              recommendation.replies = [];
            }
            recommendation.replies.push(returnedReply);
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.error(e);
          this._snackBar.open('Failed to post reply.', 'Close');
          return of(null);
        })
      ).subscribe();
    }
  }

  public deleteRecommendationReply(reply: Reply<Implementation>, recommendation: Recommendation<Implementation>): void {
    this._implementationService.deleteRecommendationReply(reply).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const replies = recommendation.replies.filter(r => r.id !== reply.id);
          recommendation.replies = replies;
        } else {
          this._snackBar.open('Failed to delete reply.', 'Close');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to delete reply.', 'Close');
        return of(null);
      })
    ).subscribe();
  }

  public cancelEditReply() {
    this.editedReply = undefined;
  }

  public openReplyEdit(reply) {
    this.editedReply = reply;
    this.editReplyForm.patchValue(reply);
  }

  public claimOwnership(): void {
    const innovator = this.innovatorService.innovator$.value;
    this.implementation.innovator = innovator;
    this._implementationService.postImplementation(this.implementation).pipe(
      first(),
      tap((implementation: Implementation) => {
        this.implementation = implementation;
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to claim ownership.', 'Close');
        return of(null);
      })
    ).subscribe();
  }
}
