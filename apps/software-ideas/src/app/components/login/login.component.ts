import { Component, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { tap, first, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Innovator } from 'src/app/models/innovator';
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
    private readonly _router: Router,
    private readonly _innovatorService: InnovatorService,
    private readonly _urlService: URLService
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
    const innovator: Innovator = form.value;
    this.loginFailed = false;
    this._authenticationService.login(innovator.emailAddress).pipe(
      tap((loggedIn: boolean) => {
        if (loggedIn) {
          let url = ('/login' !== this._urlService.currentURL ? this._urlService.currentURL : this._urlService.previousURL);
          this._router.navigate([url]);
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

  createAccount(form: FormGroup): void {
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
