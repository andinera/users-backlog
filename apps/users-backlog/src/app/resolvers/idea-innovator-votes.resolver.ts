import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

import { IdeaService } from '../services/idea.service';


@Injectable({
  providedIn: 'root'
})
export class IdeaInnovatorVotesResolver implements Resolve<any> {

  constructor(
    private readonly _ideaService: IdeaService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    return this._ideaService.getInnovatorVotes(Number(route.paramMap.get('id'))).pipe(
      first()
    );
  }
}