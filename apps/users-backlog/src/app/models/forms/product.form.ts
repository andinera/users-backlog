import { FormControl, Validators, ValidatorFn, AbstractControl } from '@angular/forms';

const urlRegex = new RegExp('^http(s)?:\/\/.+[\.].{2,}');
function urlValidator(): ValidatorFn {
    return (control: AbstractControl): {[key: string]: any} | null => {
        const allowed = urlRegex.test(control.value);
        return allowed ? null : {'invalidURL': {value: control.value}};
    }
}

export class ProductForm {
    id = new FormControl(0);
    url = new FormControl('', [
        Validators.required,
        urlValidator()
    ]);
    description = new FormControl('');
}