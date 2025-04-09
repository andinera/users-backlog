import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { InnovatorService } from 'src/app/services/innovator.service';
import { Innovator } from 'src/app/models/innovator';
import { URLService } from 'src/app/services/url.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent {

  loginForm: FormGroup;
  loginFailed = false;

  constructor(
    private readonly innovatorService: InnovatorService,
    private readonly urlService: URLService,
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

  login(form: FormGroup): void {
    const innovator: Innovator = form.value;
    this.innovatorService.getInnovator(innovator.emailAddress).subscribe((innovator: Innovator) => {
      if (innovator) {
        this.router.navigate([this.urlService.previousURL]);
      } else {
        this.loginFailed = true;
      }
    });
  }

}
