import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, forkJoin } from 'rxjs';
import { map, mergeMap } from 'rxjs/operators';

import { Implementation } from '../models/implementation.model';
import { ImplementationService } from '../services/implementation.service';
import { URLService } from '../services/url.service';
import { Category } from '../models/category.model';
import { CategoriesResolver } from './categories.resolver';

@Injectable({
  providedIn: 'root'
})
export class ImplementationsResolver implements Resolve<Category[]> {

  constructor(
    private readonly _categoriesResolver: CategoriesResolver,
    private readonly _implementationService: ImplementationService,
    private readonly _urlService: URLService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Category[]> {
    return this._categoriesResolver.resolve(route, state).pipe(
      mergeMap((categories: Category[]) => {
        return forkJoin(
          categories.map((category: Category) => {
            return this._implementationService.getImplementations(category.name).pipe(
              map((implementations: Implementation[]) => {
                category.implementations = implementations;
                return category;
              })
            );
          })
        );
      })
    );
  }
}
