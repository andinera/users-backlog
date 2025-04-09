import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { InnovatorService } from '../services/innovator.service';


@Injectable({
  providedIn: 'root'
})
export class LogInGuard implements CanActivate {

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _router: Router
  ) {

  }
  
  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): boolean {
      const innovator = this._innovatorService.innovator$.value;
      if (innovator) {
        return true;
      } else {
        this._router.navigate(['Log In']);
      }
    }
  
}
