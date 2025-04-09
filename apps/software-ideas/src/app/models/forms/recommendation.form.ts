import { FormControl, Validators } from '@angular/forms';

export class RecommendationForm {
    message = new FormControl('', [
        Validators.required
    ]);
}