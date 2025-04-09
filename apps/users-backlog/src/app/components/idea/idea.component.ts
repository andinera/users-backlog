import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';

import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError, finalize } from 'rxjs/operators';

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
  ideaVote: boolean;
  recommendationVotes: any;
  
  recommendationForm: FormGroup;
  editRecommendationForm: FormGroup;
  editedRecommendation: Recommendation<Idea>;
  postingRecommendation = false;
  deletingRecommendation = false;

  replyForm: FormGroup;
  editReplyForm: FormGroup;
  editedReply: Reply<Idea>;
  postingReply = false;
  deletingReply = false;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    public readonly innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.recommendationForm = this._formBuilder.group(new RecommendationForm());
    this.editRecommendationForm = this._formBuilder.group(new RecommendationForm());

    this.replyForm = this._formBuilder.group(new ReplyForm());
    this.editReplyForm = this._formBuilder.group(new ReplyForm());
    
    this._route.data.pipe(
      first(),
      tap((data: {idea: Idea, votes: any}) => {
        this.idea = data.idea;
        this.ideaVote = data.votes.ideas[this.idea.id];
        this.recommendationVotes = data.votes.recommendations;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load idea and/or votes.', 'Close');
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
          this._snackBar.open('Failed to delete idea.', 'Close');
        }
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to delete idea.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  public postVote(up: boolean) {
    if (this.ideaVote != up) {
      this._ideaService.postVote(this.idea, up).pipe(
        first(),
        tap((votes: number) => {
          this.idea.votes = votes;
          this.ideaVote = up;
        }),
        catchError((error: any) => {
          console.error(error);
          this._snackBar.open('Failed to cast vote.', 'Close');
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    }
  }

  public postRecommendation(recommendationForm: FormGroup): void {
    const recommendation = recommendationForm.value;
    if (recommendationForm.controls.message.value.length === 0) {
      recommendationForm.markAllAsTouched();
    } else {
      this.postingRecommendation = true;
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
          console.error(e);
          this._snackBar.open('Failed to post recommendation.', 'Close');
          return of(null);
        }),
        finalize(() => {
          this.postingRecommendation = false;
        })
      ).subscribe();
    }
  }

  public deleteRecommendation(recommendation: Recommendation<Idea>): void {
    this.deletingRecommendation = true;
    this._ideaService.deleteRecommendation(recommendation).pipe(
      first(),
      tap((deleted: boolean) => {
        if(deleted) {
          const recommendations = this.idea.recommendations.filter(r => r.id !== recommendation.id);
          this.idea.recommendations = recommendations;
        } else {
          this._snackBar.open('Failed to delete recommendation.', 'Close');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.error(e);
        this._snackBar.open('Failed to delete recommendation.', 'Close');
        return of(null);
      }),
      finalize(() => {
        this.deletingRecommendation = false;
      })
    ).subscribe();
  }

  public postRecommendationVote(recommendation: Recommendation<Idea>, up: boolean) {
    if (this.recommendationVotes[recommendation.id] != up) {
      this._ideaService.postRecommendationVote(recommendation, up).pipe(
        first(),
        tap((votes: number) => {
          recommendation.votes = votes;
          this.recommendationVotes[recommendation.id] = up;
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.error(e);
          this._snackBar.open('Failed to cast vote.', 'Close');
          return of(null);
        })
      ).subscribe();
    }
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
      this.postingReply = true;
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
          console.error(e);
          this._snackBar.open('Failed to post reply.', 'Close');
          return of(null);
        }),
        finalize(() => {
          this.postingReply = false;
        })
      ).subscribe();
    }
  }

  public deleteRecommendationReply(reply: Reply<Idea>, recommendation: Recommendation<Idea>): void {
    this.deletingReply = true;
    this._ideaService.deleteRecommendationReply(reply).pipe(
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
      }),
      finalize(() => {
        this.deletingReply = false;
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
