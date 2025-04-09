import { Component, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ReplaySubject } from 'rxjs';

import { URLService } from 'src/app/services/url.service';
import { LoginForm } from 'src/app/models/forms/login.form';
import { InnovatorService } from 'src/app/services/innovator.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnDestroy {

  public loginForm: FormGroup;
  public loginFailed = false;
  public createAccountFailed = false;
  public errorMessage: string;
  public successMessage: string;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder,
    private readonly _urlService: URLService,
    private readonly _router: Router
  ) {
    this.loginForm = this._formBuilder.group(new LoginForm());
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  public createAccount(value: {emailAddress: string, password: string}): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      this._innovatorService.createUser(value.emailAddress, value.password)
      .then(success => {
        this.errorMessage = null;
        this.successMessage = success;
      }).catch(error => {
        this.successMessage = null;
        this.errorMessage = error;
      });
    }
  }

  public login(value: {emailAddress: string, password: string}): void {
    if(this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
    } else {
      this.loginFailed = false;
      this._innovatorService.logIn(value.emailAddress, value.password)
      .then(credential => {
        this._router.navigate([this._urlService.previousURL]);
      }).catch(error => {
        this.successMessage = null;
        this.errorMessage = error;
      });
    }
  }

  public openGoogleAuthenticator() {
    history.replaceState(history.state, '', this._urlService.previousURL);
    this._innovatorService.signInWithGoogle();
  }

}
