import { FormControl } from '@angular/forms';

export class ReplyForm {
    id = new FormControl(0);
    message = new FormControl('');
    innovator = new FormControl(undefined);
    recommendation = new FormControl(undefined);
    dateTimeCreated = new FormControl(undefined);
}