import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';

import { URLService } from '../services/url.service';
import { InnovatorService } from '../services/innovator.service';

@Injectable({
  providedIn: 'root'
})
export class RedirectGuard implements CanActivate {

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _router: Router,
    private readonly _urlService: URLService
  ) {

  }
  
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
      const innovator = this._innovatorService.innovator$.value;
      if (innovator) {
        this._router.navigate([this._urlService.previousURL]);
      } else {
        return true;
      }
    }
  
}
