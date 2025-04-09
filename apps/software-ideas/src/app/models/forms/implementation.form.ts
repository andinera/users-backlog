import { FormControl, Validators } from '@angular/forms';

export class ImplementationForm {
    source = new FormControl('');
    name = new FormControl('', [
      Validators.required
    ]);
    categories = new FormControl([], [
        Validators.required
    ]);
}