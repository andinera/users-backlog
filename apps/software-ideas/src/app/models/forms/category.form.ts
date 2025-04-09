import { FormControl, Validators } from '@angular/forms';

export class CategoryForm {
    name = new FormControl('', [
        Validators.required
    ]);
}