import { Injectable } from '@angular/core';
import { Resolve, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first, map } from 'rxjs/operators';

import { Category } from '../models/category.model';
import { CategoryService } from '../services/category.service';
import { URLService } from '../services/url.service';

@Injectable({
  providedIn: 'root'
})
export class CategoriesResolver implements Resolve<Category[]> {

  constructor(
    private readonly _categoryService: CategoryService,
    private readonly _router: Router,
    private readonly _urlService: URLService
  ) { }
  
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Category[]> {
    return this._categoryService.getAllCategories().pipe(
      first(),
      map((categories: Category[]) => {
        if (categories) {
          return categories;
        } else {
          this._router.navigate([this._urlService.previousURL]);
        }
      })
    );
  }
}
