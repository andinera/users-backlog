import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormGroup, FormBuilder } from '@angular/forms';
import { first, tap, takeUntil, catchError } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

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

  implementationForm: FormGroup;
  categoryForm: FormGroup;
  categoryOptions: Category[];
  loggedIn = false;

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _formBuilder: FormBuilder,
    private readonly _implementationService: ImplementationService,
    private readonly _innovatorService: InnovatorService,
    private readonly _route: ActivatedRoute,
    private readonly _router: Router
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
        console.log(error);
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
      const implementation = implementationForm.value;
      const innovator = this._innovatorService.innovator$.value;
      if (implementationForm.controls.isOwner.value) {
        const innovator = this._innovatorService.innovator$.value;
        implementation.innovator = innovator;
      } else {
        implementation.innovator = null;
      }
      this._implementationService.postImplementation(implementation).pipe(
        first(),
        tap((newImplementation: Implementation) => {
          if (newImplementation) {
            this._router.navigate([`/Implementation/${newImplementation.name}`])
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.log(e);
          return of(null);
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
