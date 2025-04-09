import { FormControl, Validators } from '@angular/forms';

export class LoginForm {
    emailAddress = new FormControl('', [
        Validators.required,
        Validators.email
    ]);
    password = new FormControl('', [
        Validators.required
    ]);
}