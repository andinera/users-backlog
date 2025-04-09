import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, ReplaySubject } from 'rxjs';
import { first, tap, takeUntil, catchError, finalize } from 'rxjs/operators';

import { Implementation } from 'src/app/models/implementation.model';
import { ImplementationService } from 'src/app/services/implementation.service';
import { Category } from 'src/app/models/category.model';
import { ImplementationForm } from 'src/app/models/forms/implementation.form';
import { CategoryForm } from 'src/app/models/forms/category.form';
import { InnovatorService } from 'src/app/services/innovator.service';

@Component({
  selector: 'app-new-implementation [idea]',
  templateUrl: './new-implementation.component.html',
  styleUrls: ['./new-implementation.component.css']
})
export class NewImplementationComponent implements OnInit {

  categoryForm: FormGroup;
  categoryOptions: Category[];

  implementationForm: FormGroup;
  postingImplementation = false;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _implementationService: ImplementationService,
    private readonly _innovatorService: InnovatorService,
    private readonly _route: ActivatedRoute,
    private readonly _router: Router,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {
    this.implementationForm = this._formBuilder.group(new ImplementationForm())
    this.categoryForm = this._formBuilder.group(new CategoryForm());

    // Gather data from resolver
    this._route.data.pipe(
      first(),
      tap((data: {categories: Category[]}) => {
        this.categoryOptions = data.categories;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load categories.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();

    if (this._implementationService.implementationForEditing) {
      this.implementationForm.patchValue(this._implementationService.implementationForEditing);
      this.implementationForm.controls.categories.setValue(this.categoryOptions.filter(category => this._implementationService.implementationForEditing.categories.map(cat => cat.name).includes(category.name)));
      this.implementationForm.controls.isOwner.setValue(!!this._implementationService.implementationForEditing.innovator);
      this._implementationService.implementationForEditing = undefined;
    }
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  addImplementation(implementationForm: FormGroup): void {
    if (implementationForm.invalid) {
      implementationForm.markAllAsTouched();
    } else {
      this.postingImplementation = true;
      const implementation = implementationForm.value;
      if (implementationForm.controls.isOwner.value) {
        implementation.innovator = this._innovatorService.innovator$.value;
      }
      this._implementationService.postImplementation(implementation).pipe(
        first(),
        tap((newImplementation: Implementation) => {
          if (newImplementation) {
            this._router.navigate(['/implementation', newImplementation.id, newImplementation.name])
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.error(e);
          this._snackBar.open('Failed to create implementation.', 'Close');
          return of(null);
        }),
        finalize(() => {
          this.postingImplementation = false;
        })
      ).subscribe();
    }
  }

  addCategory(category: Category) {
    const categoryOptionsNames = this.categoryOptions.map(category => category.name);
    if (categoryOptionsNames.includes(category.name)) {
      category = this.categoryOptions.filter(options => options.name === category.name)[0];
    } else {
      this.categoryOptions.push(category);
    }
    const selectedCategoryNames = this.implementationForm.value.categories.map(category => category.name);
    if (!selectedCategoryNames.includes(category.name)) {
      const selectedCategories = this.implementationForm.controls.categories.value;
      selectedCategories.push(category);
      this.implementationForm.controls.categories.setValue(selectedCategories);
    }
    this.categoryForm.reset();
  }

}
