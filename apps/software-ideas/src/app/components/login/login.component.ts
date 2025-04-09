import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router } from '@angular/router';

import { InnovatorService } from 'src/app/services/innovator.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {

  loginForm: FormGroup;

  constructor(
    private readonly innovatorService: InnovatorService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router
  ) {
    this.loginForm = this.formBuilder.group({
      emailAddress: ''
    });
  }

  ngOnInit(): void {
  }

  login(form: FormGroup): void {
    const innovator: Innovator = form.value;
    this.innovatorService.getInnovator(innovator).subscribe((innovator: Innovator) => {
      if (innovator) {
        this.router.navigate([this.innovatorService.redirectUrl]);
      } else {
        this.router.navigate(['lost']);
      }
    });
  }

}
