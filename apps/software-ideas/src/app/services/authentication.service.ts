import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject, of, ReplaySubject} from 'rxjs';
import { tap, catchError, takeUntil } from 'rxjs/operators';

import { InnovatorService } from './innovator.service';
import { Innovator } from '../models/innovator.model';

declare var gapi: any;
declare var firebase: any;
var googleAuthProvider = new firebase.auth.GoogleAuthProvider();

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService implements OnDestroy {

  get innovator(): Observable<Innovator> {
    return this._innovator$.asObservable();
  }

  private _innovator$ = new ReplaySubject<Innovator>(1);
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _innovatorService: InnovatorService,
    private readonly _ngZone: NgZone
  ) {

    gapi.load('client', this.initializeGoogleApi.bind(this));

    firebase.auth().useDeviceLanguage();
    firebase.auth().onAuthStateChanged(user => {
      _ngZone.run(() => {
        if (user && !user.emailVerified) {
          user.sendEmailVerification();
          this._setInnovator(null);
        } else {
          this._setInnovator(user);
        }
      });
    });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  private _setInnovator(user) {
    if (user) {
      this._innovatorService.getInnovator(undefined, user.email).pipe(
        tap((innovator: Innovator) => {
          if (innovator) {
            this._innovator$.next(innovator);
          } else {
            const innovator = {
              emailAddress: user.email,
              displayName: user.displayName
            } as Innovator;
            this._innovatorService.postInnovator(innovator).pipe(
              tap((postedInnovator: Innovator) => {
                this._innovator$.next(postedInnovator);
              }),
              catchError((error: any) => {
                console.log(error);
                return of(null);
              }),
              takeUntil(this._destroyed$)
            ).subscribe();
          }
        }),
        catchError((error: any) => {
          console.log(error);
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    } else {
      this._innovator$.next(null);
    }
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
          console.log("Google API Not Initialized");
          console.log(reason.result.error.message);
        });
      });
    });
  }

  public createUser(emailAddress: string, password: string): Promise<string> {
    this.logOut();
    return new Promise((resolve, reject) => {
      this._ngZone.run(() => {
        firebase.auth().createUserWithEmailAndPassword(emailAddress, password)
        .then(credential => {
          return this._innovatorService.postInnovator({emailAddress: emailAddress} as Innovator).toPromise();
        }).then(innovator => {
          resolve("Account created. A verification email has been sent to your email address.");
        }).catch(error => {
          reject(error.message);
        });
      });
    });
  }

  public logIn(emailAddress: string, password: string): Promise<string> {
    this.logOut();
    return new Promise((resolve, reject) => {
      this._ngZone.run(() => {
        firebase.auth().signInWithEmailAndPassword(emailAddress, password)
        .then(credential => {
          if (!credential || !credential.user) {
            reject("Unknown credential.");
          } else if (!credential.user.emailVerified) {
            reject("Email address has not been verified.");
          }
          resolve("");
        }).catch(error => {
          console.log(error);
          reject("Invalid email address or password.");
        });
      });
    });
  };

  public signInWithGoogle(): void {
    this.logOut();
    firebase.auth().signInWithRedirect(googleAuthProvider);
  }

  public logOut(): void {
    this._setInnovator(null);
    firebase.auth().signOut();
  }
}
