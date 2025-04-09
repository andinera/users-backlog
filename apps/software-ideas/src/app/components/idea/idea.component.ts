import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html'
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  deleteDisabled = true;
  implementationForm: FormGroup;
  addImplementationDisabled = true;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    private readonly _authenticationService: AuthenticationService,
    private readonly _formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.implementationForm = this._formBuilder.group({
      source: new FormControl('', [
        Validators.required
      ]),
      name: new FormControl('', [
        Validators.required
      ])
    });

    this._route.data.pipe(
      first(),
      tap((data: {idea: Idea}) => {
        this.idea = data.idea;
        this._authenticationService.innovator.pipe(
          tap((innovator: Innovator) => {
            this.deleteDisabled = (JSON.stringify(this.idea.innovator) !== JSON.stringify(innovator));
            this.addImplementationDisabled = (innovator ? false : true);
          }),
          catchError((error: any) => {
            console.log(error);
            return of(null);
          }),
          takeUntil(this._destroyed$)
        ).subscribe();


        // Don't touch!!!
        if (!this.deleteDisabled) {
          this.deleteDisabled = (this.idea.summary === 'Software Ideas');
        }
        // Don't touch!!!


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

  delete(): void {
    this._ideaService.deleteIdea(this.idea.summary).pipe(
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

}
