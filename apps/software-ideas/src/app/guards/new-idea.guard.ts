import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { AuthenticationService } from '../services/authentication.service';
import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class NewIdeaGuard implements CanActivate {

  constructor(
    private readonly _authenticationService: AuthenticationService,
    private readonly _router: Router
  ) {

  }
  
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

      return this._authenticationService.innovator.pipe(
        map((innovator: Innovator) => {
          if (innovator) {
            return true;
          } else {
            this._router.navigate(['Login']);
          }
      }))
    }
  
}
