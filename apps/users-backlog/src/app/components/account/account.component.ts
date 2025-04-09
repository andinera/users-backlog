import { Component } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, ReplaySubject } from 'rxjs';
import { catchError, takeUntil, first, tap, finalize } from 'rxjs/operators';

import { InnovatorService } from 'src/app/services/innovator.service';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AccountForm } from 'src/app/models/forms/account.form';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.css']
})
export class AccountComponent {

  public accountForm: FormGroup;
  public updatingAccount = false;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    public readonly innovatorService: InnovatorService,
    private readonly _formBuilder: FormBuilder,
    private readonly _snackBar: MatSnackBar
  ) {
    this.accountForm = this._formBuilder.group(new AccountForm());
    this.accountForm.patchValue(innovatorService.innovator$.value);
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  public updateAccount() {
    if (this.accountForm.invalid) {
      this.accountForm.markAllAsTouched();
    } else {
      this.updatingAccount = true;
      const innovator = Object.assign({}, this.innovatorService.innovator$.value);
      innovator.emailAddress = this.accountForm.controls.emailAddress.value;
      innovator.hideEmailAddress = this.accountForm.controls.hideEmailAddress.value;
      innovator.displayName = this.accountForm.controls.displayName.value;
      this.innovatorService.postInnovator(innovator).pipe(
        first(),
        tap(() => {
          this._snackBar.open('Account updated.', 'Close');
        }),
        takeUntil(this._destroyed$),
        catchError((error: any) => {
          console.error(error);
          this._snackBar.open('Failed to claim ownership.', 'Close');
          return of(null);
        }),
        finalize(() => {
          this.updatingAccount = false;
        })
      ).subscribe();
    }
  }

}
