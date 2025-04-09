import { Injectable, NgZone } from '@angular/core';
import { Observable, BehaviorSubject} from 'rxjs';
import { map } from 'rxjs/operators';

import { InnovatorService } from './innovator.service';
import { Innovator } from '../models/innovator';

declare var gapi: any;

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  get innovator(): Observable<Innovator> {
    return this._innovator$;
  }

  private _innovator$ = new BehaviorSubject<Innovator>(null);

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _ngZone: NgZone
  ) {
    gapi.load('client', this.initializeGoogleApi.bind(this));
   }

  private initializeGoogleApi(): Promise<any> {
      return new Promise((resolve, reject) => {
          this._ngZone.run(() => {
              gapi.client.init({
                  apiKey: 'AIzaSyDSq3qOHBHL9YFahSGB5pO3koCIMUgTtuM',
                  discoveryDocs: [],
                  clientId: undefined,
                  scopes: undefined
              }).then((response: any) => {
                  console.log("Google API Initialized");
              }, (reason: any) => {
                console.log("Google API NOT Initialized");
                  console.log(reason.result.error.message);
              });
          });
      });
  }

  login(emailAddress: string): Observable<boolean> {
    return this._innovatorService.getInnovator(emailAddress).pipe(
      map((innovator: Innovator) => {
        if (innovator) {
          this._innovator$.next(innovator);
        }
        return (!!innovator);
      })
    );

    // console.log(gapi.auth2);
    // console.log(gapi.auth2.getAuthInstance());
    // gapi.auth2.getAuthInstance().signIn();
    // gapi.auth2.getAuthInstance().signOut();
    // gapi.auth2.getAuthInstance().isSignedIn.listen(callback);
    // gapi.auth2.getAuthInstance().isSignedIn.get();
  }

  logout(): void {
    this._innovator$.next(null);
  }
}
