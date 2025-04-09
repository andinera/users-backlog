import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatTabChangeEvent } from '@angular/material/tabs';

import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea.model';
import { Innovator } from 'src/app/models/innovator.model';
import { RecommendationForm } from 'src/app/models/forms/recommendation.form';
import { Recommendation } from 'src/app/models/recommendation.model';
import { RecommendationService } from 'src/app/services/recommendation.service';
import { InnovatorService } from 'src/app/services/innovator.service';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html',
  styleUrls: ['./idea.component.css']
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  deleteDisabled = true;
  displayNewImplementationForm = false; 
  selectedIndex: number = null;
  tabLabels = ['Description', 'Implementations', 'Innovator', 'Recommendations'];
  recommendationForm: FormGroup;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    public readonly innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder,
    private readonly _recommendationService: RecommendationService
  ) { }

  ngOnInit(): void {
    this.recommendationForm = this._formBuilder.group(new RecommendationForm());
    
    // Gather data from resolver
    this._route.data.pipe(
      first(),
      tap((data: {idea: Idea}) => {
        this.idea = data.idea;
        // this.idea.recommendations.forEach(recommendation => recommendation.dateTimeCreated = new Date(Date.parse(recommendation.dateTimeCreated.toString())));
        this.innovatorService.innovator$.pipe(
          tap((innovator: Innovator) => {
              this.deleteDisabled = (!innovator || this.idea.summary === 'Software Ideas');
          }),
          catchError((error: any) => {
              console.log(error);
              return of(null);
          }),
          takeUntil(this._destroyed$)
        ).subscribe();
      }),
      catchError((error: any) => {
        console.log(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();

    // Route based on fragment
    this._route.fragment.pipe(
      tap((data: string) => {
        if (data) {
          for (let i = 0; i < this.tabLabels.length; i++) {
            if (data === this.tabLabels[i]) {
              this.selectedIndex = i;
              break;
            }
          }
        }
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onSelectedTabChange(event: MatTabChangeEvent) {
    history.replaceState(history.state, '', `${location.pathname}#${event.tab.textLabel}`);
  }

  onNewImplementationClick() {
    this.displayNewImplementationForm = !this.displayNewImplementationForm;
  }

  delete(): void {
    this._ideaService.deleteIdea(this.idea.id).pipe(
      first(),
      tap((deleted) => {
        this._router.navigateByUrl('/ideas');
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  addRecommendation(recommendation: Recommendation): void {
    if(this.recommendationForm.invalid) {
      this.recommendationForm.markAllAsTouched();
    } else {
      recommendation.idea = this.idea;
      this._recommendationService.postRecommendation(recommendation).pipe(
        first(),
        tap((newRecommendation: Recommendation) => {
          if (newRecommendation) {
            this.idea.recommendations.push(newRecommendation);
            this.recommendationForm.reset();
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

}
