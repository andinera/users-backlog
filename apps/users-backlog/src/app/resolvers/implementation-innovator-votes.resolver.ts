import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';
import { first } from 'rxjs/operators';

import { ImplementationService } from '../services/implementation.service';


@Injectable({
  providedIn: 'root'
})
export class ImplementationInnovatorVotesResolver implements Resolve<any> {

  constructor(
    private readonly _implementationService: ImplementationService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<any> {
    return this._implementationService.getInnovatorVotes(Number(route.paramMap.get('id'))).pipe(
      first()
    );
  }
}