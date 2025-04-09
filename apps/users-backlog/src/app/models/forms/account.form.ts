import { FormControl, Validators } from '@angular/forms';

export class AccountForm {
    emailAddress = new FormControl('', [
        Validators.required,
        Validators.email
    ]);
    hideEmailAddress = new FormControl(false);
    password = new FormControl('');
    verifiedPassword = new FormControl('');
    displayName = new FormControl('');
}