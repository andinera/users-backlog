import { FormControl, Validators } from '@angular/forms';

export class IdeaForm {
    summary = new FormControl('', [
        Validators.required
    ]);
    description = new FormControl('');
    categories = new FormControl([], [
        Validators.required
    ]);
}