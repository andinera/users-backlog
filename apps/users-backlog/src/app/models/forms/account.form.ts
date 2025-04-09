import { FormControl, Validators } from '@angular/forms';

export class AccountForm {
    emailAddress = new FormControl('', [
        Validators.required,
        Validators.email
    ]);
    password = new FormControl('');
    verifiedPassword = new FormControl('');
    displayName = new FormControl('');
}