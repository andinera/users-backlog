import { Component, OnInit, OnDestroy } from '@angular/core';
import { tap, takeUntil, catchError, first } from 'rxjs/operators';
import { ReplaySubject, of } from 'rxjs';

import { CategoryService } from 'src/app/services/category.service';
import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { Category } from 'src/app/models/category';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html',
  styleUrls: ['./ideas.component.css']
})
export class IdeasComponent implements OnInit, OnDestroy {
  
  title = 'project-ideas';
  categoriesOfIdeas = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor (
    private readonly _ideaService: IdeaService,
    private readonly _categoryService: CategoryService
  ) {
  }

  ngOnInit() {
    this._categoryService.getAllCategories().pipe(
      first(),
      tap((categories: Category[]) => {
        if (categories) {
          categories.forEach((category: Category) => {
            this._ideaService.getIdeas(category.name).pipe(
              first(),
              tap((ideas: Idea[]) => {
                if (ideas) {
                  this.categoriesOfIdeas.push({category: category,
                                               ideas: ideas});
                }
              }),
              takeUntil(this._destroyed$),
              catchError((e: any) => {
                console.log(e);
                return of(null);
              })
            ).subscribe();
          })
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }
}
