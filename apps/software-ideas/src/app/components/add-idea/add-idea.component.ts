import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { tap, catchError, first, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { IdeaService } from 'src/app/services/idea.service';
import { Idea } from 'src/app/models/idea';
import { Category } from 'src/app/models/category';
import { IdeaForm } from 'src/app/models/forms/idea.form';
import { CategoryForm } from 'src/app/models/forms/category.form';

@Component({
  selector: 'app-add-idea',
  templateUrl: './add-idea.component.html'
})
export class AddIdeaComponent implements OnInit, OnDestroy {
  
  categoryForm: FormGroup;
  ideaForm: FormGroup;
  categoryOptions: Category[];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _formBuilder: FormBuilder,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute
  ) {
  }

  ngOnInit(): void {
    this.categoryForm = this._formBuilder.group(new CategoryForm());
    this.ideaForm = this._formBuilder.group(new IdeaForm());

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
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onSubmit(idea: Idea) {
    console.log(idea);
    this._ideaService.postIdea(idea).pipe(
      first(),
      tap((newIdea: Idea) => {
        if (newIdea) {
          this._router.navigateByUrl('/ideas');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  addCategory(category: Category) {
    const categoryOptionsNames = this.categoryOptions.map(category => category.name);
    const selectedCategoryNames = this.ideaForm.value.categories.map(category => category.name);
    if (!categoryOptionsNames.includes(category.name)) {
      this.categoryOptions.push(category);
    }
    if (!selectedCategoryNames.includes(category.name)) {
      const selectedCategories = this.ideaForm.controls.categories.value;
      selectedCategories.push(category);
      this.ideaForm.controls.categories.setValue(selectedCategories);
    }
  }

}
