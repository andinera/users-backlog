import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable } from 'rxjs';

import { Implementation } from '../models/implementation.model';
import { ImplementationService } from '../services/implementation.service';

@Injectable({
  providedIn: 'root'
})
export class ImplementationsResolver implements Resolve<Implementation[]> {

  constructor(
    private readonly _implementationService: ImplementationService
  ) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Implementation[]> {
    return this._implementationService.getImplementations();
  }
}
