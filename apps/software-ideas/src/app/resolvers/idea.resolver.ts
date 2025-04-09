import { Injectable } from '@angular/core';
import { Router, Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { of, Observable, EMPTY } from 'rxjs';
import { mergeMap, first } from 'rxjs/operators';

import { IdeaService } from '../services/idea.service';
import { Idea } from '../models/idea';
import { URLService } from '../services/url.service';

@Injectable({
    providedIn: 'root'
  })
  export class IdeaResolver implements Resolve<Idea> {
  
    constructor(
      private readonly _ideaService: IdeaService,
      private readonly _router: Router,
      private readonly _urlService: URLService
    ) { }
  
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Idea> | Observable<never> {
      return this._ideaService.getIdea(route.paramMap.get('summary')).pipe(
        first(),
        mergeMap((idea: Idea) => {
          if (idea) {
            return of(idea);
          } else {
            this._router.navigate([this._urlService.previousURL]);
            return EMPTY;
          }
        })
      )
    }
  }