import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, Resolve, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

import { IdeaService } from '../services/idea.service';
import { URLService } from '../services/url.service';
import { Idea } from '../models/idea';

@Injectable({
  providedIn: 'root'
})
export class CategoryResolver implements Resolve<Idea[]> {

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _urlService: URLService
  ) { }
  
  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Idea[]> | Observable<never> {
    return this._ideaService.getIdeas(route.paramMap.get('categoryName')).pipe(
      first(),
      mergeMap((ideas: Idea[]) => {
        if (ideas) {
          return of(ideas);
        } else {
          this._router.navigate([this._urlService.previousURL]);
          return EMPTY;
        }
      })
    );
  }
}
