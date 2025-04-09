import { FormControl, Validators } from '@angular/forms';

export class IdeaForm {
    id = new FormControl(0);
    summary = new FormControl('', [
        Validators.required
    ]);
    description = new FormControl('');
    categories = new FormControl([], [
        Validators.required
    ]);
}