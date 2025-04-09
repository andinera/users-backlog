import { FormControl } from '@angular/forms';

export class RecommendationForm {
    id = new FormControl(0);
    message = new FormControl('');
    innovator = new FormControl(undefined);
    implementation = new FormControl(undefined);
    dateTimeCreated = new FormControl(undefined);
}