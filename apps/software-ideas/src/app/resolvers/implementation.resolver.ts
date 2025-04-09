import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { first, mergeMap } from 'rxjs/operators';

import { Implementation } from '../models/implementation';
import { ImplementationService } from '../services/implementation.service';
import { URLService } from '../services/url.service';

@Injectable({
  providedIn: 'root'
})
export class ImplementationResolver implements Resolve<Implementation> {

  constructor(
    private readonly _implementationService: ImplementationService,
    private readonly _urlService: URLService,
    private readonly _router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Implementation> | Observable<never> {
    return this._implementationService.getImplementation(route.paramMap.get('name')).pipe(
      first(),
      mergeMap((implementation: Implementation) => {
        if (implementation) {
          return of(implementation);
        } else {
          this._router.navigate([this._urlService.previousURL]);
          return EMPTY;
        }
      })
    );
  }
}
