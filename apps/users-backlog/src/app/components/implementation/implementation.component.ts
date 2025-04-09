import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
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

@Component({
  selector: 'app-implementation',
  templateUrl: './implementation.component.html',
  styleUrls: ['./implementation.component.css']
})
export class ImplementationComponent implements OnInit, OnDestroy {

  implementation: Implementation;
  editedRecommendation: Recommendation;
  recommendationForm: FormGroup;
  editRecommendationForm: FormGroup;
  replyForm: FormGroup;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _implementationService: ImplementationService,
    private readonly _route: ActivatedRoute,
    private readonly _router: Router,
    private readonly _formBuilder: FormBuilder,
    public readonly innovatorService: InnovatorService,
    private readonly _sessionStorageService: SessionStorageService
  ) { }

  ngOnInit(): void {
    this.recommendationForm = this._formBuilder.group(new RecommendationForm());
    this.editRecommendationForm = this._formBuilder.group(new RecommendationForm());
    this.replyForm = this._formBuilder.group(new ReplyForm());

    this._route.data.pipe(
      first(),
      tap((data: {implementation: Implementation}) => {
        this.implementation = data.implementation;
        this.implementation.recommendations.forEach(recommendation => recommendation.dateTimeCreated = new Date(Date.parse(recommendation.dateTimeCreated.toString())));

        let parameters = this._sessionStorageService.retrieveData('postVote');
        if (parameters) {
          this.postVote(parameters.up);
        }
    
        parameters = this._sessionStorageService.retrieveData('postRecommendation');
        if (parameters) {
          this.recommendationForm.patchValue(parameters.recommendation);
          this.postRecommendation(this.recommendationForm);
        }
    
        parameters = this._sessionStorageService.retrieveData('postRecommendationVote');
        if (parameters) {
          const recommendation = this.implementation.recommendations.filter(r => r.id = parameters.recommendationId)[0];
          this.postRecommendationVote(recommendation, parameters.up);
        }
    
        parameters = this._sessionStorageService.retrieveData('postRecommendationReply');
        if (parameters) {
          this.replyForm.patchValue(parameters.reply);
          const recommendation = this.implementation.recommendations.filter(r => r.id = parameters.reply.recommendation.id)[0];
          this.postReply(this.replyForm, recommendation);
        }
      }),
      catchError((error: any) => {
        console.log(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  public editImplementation() {
    this._implementationService.implementationForEditing = this.implementation;
    this._router.navigate(['edit-implementation']);
  }

  public postVote(up: boolean) {
    this._implementationService.postVote(this.implementation.id, up).pipe(
      first(),
      tap((votes: number) => {
        this.implementation.votes = votes;
      }),
      catchError((error: any) => {
        console.log(error);
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
      const innovator = this.innovatorService.innovator$.value;
      recommendation.innovator = innovator;
      recommendation.implementation = this.implementation;
      this._implementationService.postRecommendation(recommendation).pipe(
        first(),
        tap((returnedRecommendation: Recommendation) => {
          recommendationForm.reset();
          if (recommendation.id > 0) {
            const filteredRecommendations = this.implementation.recommendations.filter(r => r.id === recommendation.id);
            const index = this.implementation.recommendations.indexOf(filteredRecommendations[0]);
            this.implementation.recommendations.splice(index, 1, returnedRecommendation);
            this.editedRecommendation = undefined;
          } else {
            this.implementation.recommendations.push(returnedRecommendation);
          }
          returnedRecommendation.dateTimeCreated = new Date(Date.parse(returnedRecommendation.dateTimeCreated.toString()));
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.log(e);
          return of(null);
        })
      ).subscribe();
    }
  }

  public postRecommendationVote(recommendation: Recommendation, up: boolean) {
    recommendation.implementation = this.implementation;
    this._implementationService.postRecommendationVote(recommendation.id, up).pipe(
      first(),
      tap((votes: number) => {
        recommendation.votes = votes;
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  public cancelEditRecommendtion() {
    this.editedRecommendation = undefined;
  }

  public openEdit(recommendation) {
    this.editedRecommendation = recommendation;
    this.editRecommendationForm.patchValue(recommendation);
  }

  public postReply(replyForm: FormGroup, recommendation: Recommendation) {
    const reply = replyForm.value;
    if (!replyForm.controls.message.value) {
      replyForm.markAllAsTouched();
    } else {
      const innovator = this.innovatorService.innovator$.value;
      reply.innovator = innovator;
      reply.recommendation = recommendation;
      this._implementationService.postRecommendationReply(reply).pipe(
        first(),
        tap((returnedReply: Reply) => {
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
          returnedReply.dateTimeCreated = new Date(Date.parse(returnedReply.dateTimeCreated.toString()));
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.log(e);
          return of(null);
        })
      ).subscribe();
    }
  }

  public claimOwnership(): void {
    const innovator = this.innovatorService.innovator$.value;
    this.implementation.innovator = innovator;
    this._implementationService.postImplementation(this.implementation).pipe(
      first(),
      tap((implementation: Implementation) => {
        console.log('test2');
        this.implementation = implementation;
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }
}
