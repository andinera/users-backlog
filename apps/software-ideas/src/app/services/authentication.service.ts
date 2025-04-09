import { Injectable, EventEmitter } from '@angular/core';
import { Observable, BehaviorSubject} from 'rxjs';
import { map } from 'rxjs/operators';

import { InnovatorService } from './innovator.service';
import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private _innovator: Innovator;
  private _innovator$ = new BehaviorSubject<Innovator>(this._innovator);

  constructor(
    private readonly _innovatorService: InnovatorService
  ) { }

  get innovator(): Observable<Innovator> {
    return this._innovator$;
  }

  login(emailAddress: string): Observable<boolean> {
    this._innovator = null;
    return this._innovatorService.getInnovator(emailAddress).pipe(
      map((innovator: Innovator) => {
        if (innovator) {
          this._innovator = innovator;
          this._innovator$.next(this._innovator);
          return true;
        } else {
          return false;
        }
      })
    );
  }

  logout(): void {
    this._innovator = null;
    this._innovator$.next(this._innovator);
  }
}
