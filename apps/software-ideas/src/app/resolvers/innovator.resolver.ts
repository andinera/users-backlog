import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { Observable } from 'rxjs';

import { URLService } from '../services/url.service';
import { Innovator } from '../models/innovator.model';
import { first, map } from 'rxjs/operators';
import { InnovatorService } from '../services/innovator.service';

@Injectable({
  providedIn: 'root'
})
export class InnovatorResolver implements Resolve<Innovator> {

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _urlService: URLService,
    private readonly _router: Router
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Innovator> {
    return this._innovatorService.getInnovator(route.paramMap.get('emailAddress')).pipe(
      first(),
      map((innovator: Innovator) => {
        if (innovator) {
          return innovator;
        } else {
          this._router.navigate([this._urlService.previousURL]);
        }
      })
    )
  }
}
