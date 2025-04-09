import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve } from '@angular/router';
import { Observable } from 'rxjs';

import { IdeaService } from '../services/idea.service';
import { CategoriesResolver } from './categories.resolver';
import { Idea } from '../models/idea.model';

@Injectable({
  providedIn: 'root'
})
export class IdeasResolver implements Resolve<Idea[]> {

  constructor(
    private readonly _categoriesResolver: CategoriesResolver,
    private readonly _ideaService: IdeaService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Idea[]> {
    return this._ideaService.getIdeas();
  }
}