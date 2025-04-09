import { Injectable, EventEmitter } from '@angular/core';
import { Observable, merge, of} from 'rxjs';
import { map } from 'rxjs/operators';

import { InnovatorService } from './innovator.service';
import { Innovator } from '../models/innovator';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private innovator$ = new EventEmitter<Innovator>();
  private _innovator: Innovator;

  constructor(
    private readonly innovatorService: InnovatorService
  ) { }

  get innovator(): Observable<Innovator> {
    return merge(of(this._innovator), this.innovator$);
  }

  login(emailAddress: string): Observable<boolean> {
    this._innovator = null;
    return this.innovatorService.getInnovator(emailAddress).pipe(
      map((innovator: Innovator) => {
        if (innovator) {
          this._innovator = innovator;
          this.innovator$.emit(this._innovator);
          return true;
        } else {
          return false;
        }
      })
    );
  }

  logout(): void {
    this._innovator = null;
    this.innovator$.emit(this._innovator);
  }
}
