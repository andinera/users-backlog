import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { AuthenticationService } from '../services/authentication.service';
import { Innovator } from '../models/innovator.model';
import { URLService } from '../services/url.service';

@Injectable({
  providedIn: 'root'
})
export class RedirectGuard implements CanActivate {

  constructor(
    private readonly _authenticationService: AuthenticationService,
    private readonly _router: Router,
    private readonly _urlService: URLService
  ) {

  }
  
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
      const innovator = this._authenticationService.innovator$.value;
      if (innovator) {
        this._router.navigate([this._urlService.previousURL]);
      } else {
        return true;
      }
    }
  
}
