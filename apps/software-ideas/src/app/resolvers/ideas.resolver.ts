import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { mergeMap, map } from 'rxjs/operators';
import { Observable, forkJoin } from 'rxjs';

import { IdeaService } from '../services/idea.service';
import { CategoriesResolver } from './categories.resolver';
import { Category } from '../models/category.model';
import { Idea } from '../models/idea.model';

@Injectable({
  providedIn: 'root'
})
export class IdeasResolver implements Resolve<Category[]> {

  constructor(
    private readonly _categoriesResolver: CategoriesResolver,
    private readonly _ideaService: IdeaService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Category[]> {
    return this._categoriesResolver.resolve(route, state).pipe(
      mergeMap((categories: Category[]) => {
        return forkJoin(
          categories.map((category: Category) => {
            return this._ideaService.getIdeas(category.name).pipe(
              map((ideas: Idea[]) => {
                category.ideas = ideas;
                return category;
              })
            );
          })
        );
      })
    );
  }
}