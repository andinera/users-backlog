import { FormControl, Validators } from '@angular/forms';

export class RecommendationForm {
    id = new FormControl(0);
    message = new FormControl('');
    innovatorId = new FormControl(0);
    implementationId = new FormControl(0);
}