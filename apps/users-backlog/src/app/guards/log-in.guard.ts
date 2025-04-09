import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';

import { AuthenticationService } from '../services/authentication.service';
import { Innovator } from '../models/innovator.model';

@Injectable({
  providedIn: 'root'
})
export class LogInGuard implements CanActivate {

  constructor(
    private readonly _authenticationService: AuthenticationService,
    private readonly _router: Router
  ) {

  }
  
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
      const innovator = this._authenticationService.innovator$.value;
      if (innovator) {
        return true;
      } else {
        this._router.navigate(['Log In']);
      }
    }
  
}
