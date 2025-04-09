import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { of, ReplaySubject } from 'rxjs';
import { tap, catchError, first, takeUntil, finalize } from 'rxjs/operators';

import { IdeaService } from 'src/app/services/idea.service';
import { Idea } from 'src/app/models/idea.model';
import { Category } from 'src/app/models/category.model';
import { IdeaForm } from 'src/app/models/forms/idea.form';
import { CategoryForm } from 'src/app/models/forms/category.form';

@Component({
  selector: 'app-new-idea',
  templateUrl: './new-idea.component.html',
  styleUrls: ['./new-idea.component.css']
})
export class NewIdeaComponent implements OnInit, OnDestroy {
  
  categoryForm: FormGroup;
  categoryOptions: Category[];
  
  ideaForm: FormGroup;
  postingIdea = false;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _formBuilder: FormBuilder,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    private readonly _snackBar: MatSnackBar
  ) {
  }

  ngOnInit(): void {
    this.categoryForm = this._formBuilder.group(new CategoryForm());
    this.ideaForm = this._formBuilder.group(new IdeaForm());

    // Gather data from resolver
    this._route.data.pipe(
      first(),
      tap((data: {categories: Category[], idea: Idea}) => {
        this.categoryOptions = data.categories;
        if (data.idea) {
          this.ideaForm.controls.id.setValue(data.idea.id);
          this.ideaForm.controls.summary.setValue(data.idea.summary);
          this.ideaForm.controls.description.setValue(data.idea.description);
          this.ideaForm.controls.categories.setValue(this.categoryOptions.filter(category => data.idea.categories.map(cat => cat.name).includes(category.name)));
        }
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load categories.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();

    if (this._ideaService.ideaForEditing) {
      this.ideaForm.patchValue(this._ideaService.ideaForEditing);
      this.ideaForm.controls.categories.setValue(this.categoryOptions.filter(category => this._ideaService.ideaForEditing.categories.map(cat => cat.name).includes(category.name)));
      this._ideaService.ideaForEditing = undefined;
    }
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onSubmit(idea: Idea) {
    if(this.ideaForm.invalid) {
      this.ideaForm.markAllAsTouched();
    } else {
      this.postingIdea = true;
      this._ideaService.postIdea(idea).pipe(
        first(),
        tap((newIdea: Idea) => {
          if (newIdea) {
            this._router.navigate(['/idea', newIdea.id, newIdea.summary]);
          }
        }),
        takeUntil(this._destroyed$),
        catchError((e: any) => {
          console.error(e);
          this._snackBar.open('Failed to create idea.', 'Close');
          return of(null);
        }),
        finalize(() => {
          this.postingIdea = false;
        })
      ).subscribe();
    }
  }

  newCategoryIsValid() {
    const categoryOptionsNames = this.categoryOptions.map(category => category.name);
    if (!this.categoryForm.controls.name.value ||
        categoryOptionsNames.includes(this.categoryForm.controls.name.value)) {
      return false;
    } else {
      return true;
    }
  }

  addCategory(category: Category) {
    const categoryOptionsNames = this.categoryOptions.map(category => category.name);
    if (!categoryOptionsNames.includes(category.name)) {
      this.categoryOptions.push(category);
    }
    const selectedCategoryNames = this.ideaForm.value.categories.map(category => category.name);
    if (!selectedCategoryNames.includes(category.name)) {
      const selectedCategories = this.ideaForm.controls.categories.value;
      selectedCategories.push(category);
      this.ideaForm.controls.categories.setValue(selectedCategories);
    }
    this.categoryForm.reset();
  }

}
