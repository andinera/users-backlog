import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { of, EMPTY, Observable } from 'rxjs';

import { URLService } from '../services/url.service';
import { Innovator } from '../models/innovator';
import { first, mergeMap } from 'rxjs/operators';
import { InnovatorService } from '../services/innovator.service';

@Injectable({
  providedIn: 'root'
})
export class InnovatorResolver implements Resolve<Innovator> {

  constructor(
    private readonly innovatorService: InnovatorService,
    private readonly urlService: URLService,
    private readonly router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Innovator> | Observable<never> {
    return this.innovatorService.getInnovator(route.paramMap.get('emailAddress')).pipe(
      first(),
      mergeMap((innovator: Innovator) => {
        if (innovator) {
          return of(innovator);
        } else {
          this.router.navigate([this.urlService.previousURL]);
          return EMPTY;
        }
      })
    )
  }
}
