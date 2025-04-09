import { Component } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Innovator } from 'src/app/models/innovator';
import { AuthenticationService } from 'src/app/services/authentication.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  loginForm: FormGroup;
  loginFailed = false;

  constructor(
    private readonly authenticationService: AuthenticationService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      emailAddress: new FormControl('', [
        Validators.required,
        Validators.email
      ])
    });
  }

  closeRoute() {
    this.router.navigate([{outlets: { login: null}}]);
  }

  login(form: FormGroup): void {
    const innovator: Innovator = form.value;
    this.loginFailed = false;
    this.authenticationService.login(innovator.emailAddress).subscribe((loggedIn: boolean) => {
      if (loggedIn) {
        this.closeRoute();
      } else {
        this.loginFailed = true;
      }
    });
  }

  createAccount(form: FormGroup): void {

  }

}
