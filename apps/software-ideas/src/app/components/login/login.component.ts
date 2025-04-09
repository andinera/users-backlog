import { Component, OnDestroy, Optional } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { tap, first, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Innovator } from 'src/app/models/innovator.model';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { InnovatorService } from 'src/app/services/innovator.service';
import { URLService } from 'src/app/services/url.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnDestroy {

  loginForm: FormGroup;
  loginFailed = false;
  createAccountFailed = false;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _authenticationService: AuthenticationService,
    private readonly _formBuilder: FormBuilder,
    private readonly _innovatorService: InnovatorService,
    private readonly _router: Router,
    private readonly _urlService: URLService,
    @Optional() private readonly _dialogRef: MatDialogRef<LoginComponent>
  ) {
    this.loginForm = this._formBuilder.group({
      emailAddress: new FormControl('', [
        Validators.required,
        Validators.email
      ])
    });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  login(form: FormGroup): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      const innovator: Innovator = form.value;
      this.loginFailed = false;
      this._authenticationService.login(innovator.emailAddress).pipe(
        tap((loggedIn: boolean) => {
          if (loggedIn) {
            if (this._dialogRef) {
              this._dialogRef.close();
            } else {
              this._router.navigate([this._urlService.previousURL]);
            }
          } else {
            this.loginFailed = true;
          }
        }),
        catchError((error: any) => {
          console.log(error);
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    }
  }

  createAccount(form: FormGroup): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      const innovator: Innovator = form.value;
      this._innovatorService.getInnovator(innovator.emailAddress).pipe(
        first(),
        tap((existingInnovator: Innovator) => {
          if (existingInnovator) {
            this.createAccountFailed = true;
          } else {
            this._innovatorService.postInnovator(innovator).pipe(
              first(),
              tap((newInnovator: Innovator) => {
                if (newInnovator) {
                  this.login(form);
                } else {
                  console.log("Failed to login after creating account.");
                }
              }),
              catchError((error: any) => {
                console.log(error);
                return of(null);
              }),
              takeUntil(this._destroyed$)
            ).subscribe();
          }
        }),
        catchError((error: any) => {
          console.log(error);
          return of(null);
        })
      ).subscribe();
    }
  }

}
