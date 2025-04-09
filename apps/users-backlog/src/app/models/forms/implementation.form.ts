import { FormControl, Validators } from '@angular/forms';

export class ImplementationForm {
    id = new FormControl(0);
    name = new FormControl('', [
      Validators.required
    ]);
    description = new FormControl('');
    categories = new FormControl([], [
        Validators.required
    ]);
    isOwner = new FormControl(false);
}