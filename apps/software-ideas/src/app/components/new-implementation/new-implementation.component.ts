import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { first, tap, takeUntil, catchError } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation';
import { ImplementationService } from 'src/app/services/implementation.service';
import { Idea } from 'src/app/models/idea';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-new-implementation [idea]',
  templateUrl: './new-implementation.component.html',
  styleUrls: ['./new-implementation.component.css']
})
export class NewImplementationComponent implements OnInit {

  @Input() idea: Idea;

  implementationForm: FormGroup;
  loggedIn = false;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _implementationService: ImplementationService,
    private readonly authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.implementationForm = this._formBuilder.group({
      source: new FormControl(''),
      name: new FormControl('', [
        Validators.required
      ])
    });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  addImplementation(implementation: Implementation): void {
    if (this.implementationForm.invalid) {
      this.implementationForm.markAllAsTouched();
    } else {
      this.authenticationService.innovator.pipe(
        tap((innovator: Innovator) => {
          implementation.innovator = innovator;
          implementation.ideas = [this.idea];
          this._implementationService.postImplementation(implementation).pipe(
            first(),
            tap((newImplementation: Implementation) => {
              if (newImplementation) {
                this.idea.implementations.push(newImplementation);
              }
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
