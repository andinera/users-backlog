import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { tap, first, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation.model';
import { ImplementationService } from 'src/app/services/implementation.service';
import { RecommendationForm } from 'src/app/models/forms/recommendation.form';
import { FormBuilder, FormGroup } from '@angular/forms';
import { Recommendation } from 'src/app/models/recommendation.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator.model';

@Component({
  selector: 'app-implementation',
  templateUrl: './implementation.component.html',
  styleUrls: ['./implementation.component.css']
})
export class ImplementationComponent implements OnInit, OnDestroy {

  implementation: Implementation;
  recommendationForm: FormGroup;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _implementationService: ImplementationService,
    private readonly _route: ActivatedRoute,
    private readonly _router: Router,
    private readonly _formBuilder: FormBuilder,
    private readonly _authenticationService: AuthenticationService,
  ) { }

  ngOnInit(): void {
    this.recommendationForm = this._formBuilder.group(new RecommendationForm())

    this._route.data.pipe(
      first(),
      tap((data: {implementation: Implementation}) => {
        this.implementation = data.implementation;
        this.implementation.recommendations.forEach(recommendation => recommendation.dateTimeCreated = new Date(Date.parse(recommendation.dateTimeCreated.toString())));
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
    this._router.navigate(['Edit Implementation']);
  }

  public postVote(up: boolean) {
    this._implementationService.postVote(this.implementation, up).pipe(
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

  public addRecommendation(recommendation: Recommendation): void {
    if (this.recommendationForm.controls.message.value.length === 0) {
      this.recommendationForm.markAllAsTouched();
    } else {
      this._authenticationService.innovator.pipe(
        tap((innovator: Innovator) => {
          recommendation.innovator = innovator;
          recommendation.implementation = this.implementation;
          this._implementationService.postRecommendation(recommendation).pipe(
            first(),
            tap((recommendation: Recommendation) => {
              this.recommendationForm.reset();
              this.implementation.recommendations.push(recommendation);
              recommendation.dateTimeCreated = new Date(Date.parse(recommendation.dateTimeCreated.toString()));

            }),
            takeUntil(this._destroyed$),
            catchError((e: any) => {
              console.log(e);
              return of(null);
            })
          ).subscribe();
        }),
        catchError((error: any) => {
          console.log(error);
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    }
  }

}
