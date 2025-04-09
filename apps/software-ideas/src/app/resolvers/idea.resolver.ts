import { Injectable } from '@angular/core';
import { Router, Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { of, Observable, EMPTY } from 'rxjs';
import { mergeMap, first } from 'rxjs/operators';

import { IdeaService } from '../services/idea.service';
import { Idea } from '../models/idea';

@Injectable({
    providedIn: 'root'
  })
  export class IdeaResolver implements Resolve<Idea> {
  
    constructor(
      private readonly ideaService: IdeaService,
      private readonly router: Router) { }
  
    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Idea> | Observable<never> {
      return this.ideaService.getIdea(route.paramMap.get('summary')).pipe(
        first(),
        mergeMap((idea: Idea) => {
          if (idea) {
            return of(idea);
          } else {
            this.router.navigate(['/ideas']);
            return EMPTY;
          }
        })
      )
    }
  }