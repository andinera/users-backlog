import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ReplaySubject, of } from 'rxjs';
import { first, tap, catchError, takeUntil, finalize } from 'rxjs/operators';

import { URLService } from 'src/app/services/url.service';
import { AccountForm } from 'src/app/models/forms/account.form';
import { InnovatorService } from 'src/app/services/innovator.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnDestroy {

  public loginForm: FormGroup;
  public loggingIn = false;
  public loginFailed = false;
  public createAccountFailed = false;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder,
    private readonly _urlService: URLService,
    private readonly _router: Router,
    private readonly _snackBar: MatSnackBar
  ) {
    this.loginForm = this._formBuilder.group(new AccountForm());
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  public createAccount(value: {emailAddress: string, password: string}): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      this.loggingIn = true;
      this._innovatorService.createUser(value.emailAddress, value.password).pipe(
        first(),
        tap(() => {
          this._snackBar.open('Account created. A verification email has been sent to your email address.', 'Close');
        }),
        catchError((error: any) => {
          console.error(error);
          this._snackBar.open('Failed to create account.', 'Close');
          return null;
        }),
        takeUntil(this._destroyed$),
        finalize(() => {
          this.loggingIn = false;
        })
      ).subscribe();
    }
  }

  public login(value: {emailAddress: string, password: string}): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      this.loginFailed = false;
      this.loggingIn = true;
      this._innovatorService.logIn(value.emailAddress, value.password).pipe(
        first(),
        tap((credentials: any) => {
          if (!credentials || !credentials.user) {
            this._snackBar.open('Unknown credentials.', 'Close');
          } else if (!credentials.user.emailVerified) {
            this._snackBar.open('Email address has not been verified.', 'Close');
            this._innovatorService.logOut();
          } else {
            this._router.navigate([this._urlService.previousURL]); 
          }       
        }),
        catchError((error: any) => {
          this._snackBar.open('Invalid email address or password.', 'Close');
          console.error(error);
          return of(null);
        }),
        takeUntil(this._destroyed$),
        finalize(() => {
          this.loggingIn = false;
        })
      ).subscribe();
    }
  }

  public openGoogleAuthenticator() {
    history.replaceState(history.state, '', this._urlService.previousURL);
    this._innovatorService.signInWithGoogle();
  }

}
