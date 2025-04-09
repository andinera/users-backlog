import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';

import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea.model';
import { RecommendationForm } from 'src/app/models/forms/recommendation.form';
import { Recommendation } from 'src/app/models/recommendation.model';
import { InnovatorService } from 'src/app/services/innovator.service';
import { Reply } from 'src/app/models/reply.model';
import { ReplyForm } from 'src/app/models/forms/reply.form';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html',
  styleUrls: ['./idea.component.css']
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  
  recommendationForm: FormGroup;
  editRecommendationForm: FormGroup;
  editedRecommendation: Recommendation<Idea>;

  replyForm: FormGroup;
  editReplyForm: FormGroup;
  editedReply: Reply<Idea>;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    public readonly innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.recommendationForm = this._formBuilder.group(new RecommendationForm());
    this.editRecommendationForm = this._formBuilder.group(new RecommendationForm());

    this.replyForm = this._formBuilder.group(new ReplyForm());
    this.editReplyForm = this._formBuilder.group(new ReplyForm());
    
    this._route.data.pipe(
      first(),
      tap((data: {idea: Idea}) => {
        this.idea = data.idea;
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

  public editIdea() {
    this._ideaService.ideaForEditing = this.idea;
    this._router.navigate(['/edit-idea']);
  }

  public deleteIdea() {
    this._ideaService.deleteIdea(this.idea).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          this._router.navigate(['/ideas']);
        } else {
          console.log('Failed to delete idea.');
        }
      }),
      catchError((error: any) => {
        console.log(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  public postVote(up: boolean) {
    this._ideaService.postVote(this.idea, up).pipe(
      first(),
      tap((votes: number) => {
        this.idea.votes = votes;
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
      recommendation.parent = this.idea;
      this._ideaService.postRecommendation(recommendation).pipe(
        first(),
        tap((returnedRecommendation: Recommendation<Idea>) => {
          recommendationForm.reset();
          if (recommendation.id > 0) {
            const filteredRecommendations = this.idea.recommendations.filter(r => r.id === recommendation.id);
            const index = this.idea.recommendations.indexOf(filteredRecommendations[0]);
            this.idea.recommendations.splice(index, 1, returnedRecommendation);
            this.editedRecommendation = undefined;
          } else {
            this.idea.recommendations.push(returnedRecommendation);
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.log(e);
          return of(null);
        })
      ).subscribe();
    }
  }

  public deleteRecommendation(recommendation: Recommendation<Idea>): void {
    this._ideaService.deleteRecommendation(recommendation).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const recommendations = this.idea.recommendations.filter(r => r.id !== recommendation.id);
          this.idea.recommendations = recommendations;
        } else {
          console.log('Failed to delete recommendation.');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  public postRecommendationVote(recommendation: Recommendation<Idea>, up: boolean) {
    this._ideaService.postRecommendationVote(recommendation, up).pipe(
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

  public openRecommendationEdit(recommendation: Recommendation<Idea>) {
    this.editedRecommendation = recommendation;
    this.editRecommendationForm.patchValue(recommendation);
  }

  public postReply(replyForm: FormGroup, recommendation: Recommendation<Idea>) {
    const reply = replyForm.value;
    if (!replyForm.controls.message.value) {
      replyForm.markAllAsTouched();
    } else {
      const innovator = this.innovatorService.innovator$.value;
      reply.innovator = innovator;
      reply.recommendation = recommendation;
      this._ideaService.postRecommendationReply(reply).pipe(
        first(),
        tap((returnedReply: Reply<Idea>) => {
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
          console.log(e);
          return of(null);
        })
      ).subscribe();
    }
  }

  public deleteRecommendationReply(reply: Reply<Idea>, recommendation: Recommendation<Idea>): void {
    this._ideaService.deleteRecommendationReply(reply).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const replies = recommendation.replies.filter(r => r.id !== reply.id);
          recommendation.replies = replies;
        } else {
          console.log('Failed to delete reply.');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
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

}
