import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of, ReplaySubject, BehaviorSubject } from 'rxjs';
import { tap, catchError, takeUntil } from 'rxjs/operators';

import { Innovator } from '../models/innovator.model';
import { Service } from './service';
import { environment } from 'src/environments/environment';
import { SessionStorageService } from './session-storage.service';


declare var firebase: any;
declare var gapi: any;


@Injectable({
  providedIn: 'root'
})
export class InnovatorService extends Service implements OnDestroy {
  
  public innovator$ = new BehaviorSubject<Innovator>(undefined);

  private readonly _googleAuthProvider = new firebase.auth.GoogleAuthProvider();
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  private _serviceURL = `${this.endpointURL}/innovator/`;

  constructor(
    private readonly _http: HttpClient,
    private readonly _ngZone: NgZone,
    private readonly _sessionStorageService: SessionStorageService
  ) {
    super();

    gapi.load('client', this.initializeGoogleApi.bind(this));

    var firebaseConfig = {
      apiKey: environment.firebaseApiKey,
      authDomain: "users-backlog.firebaseapp.com",
      databaseURL: "https://users-backlog.firebaseio.com",
      projectId: "users-backlog",
      storageBucket: "users-backlog.appspot.com",
      messagingSenderId: "809333411313",
      appId: "1:809333411313:web:ba1fd4400b4146986a4850",
      measurementId: "G-XK4M2PBMTM"
    };
    // Initialize Firebase
    var app = firebase.initializeApp(firebaseConfig);
    firebase.analytics(app);
    firebase.auth(app);

    firebase.auth().useDeviceLanguage();
    firebase.auth().onIdTokenChanged(user => {
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
      this.getInnovator(undefined, user.email).pipe(
        tap((innovator: Innovator) => {
          user.getIdToken().then(idToken => {
            if (innovator) {
              innovator.idToken = idToken;
              this.innovator$.next(innovator);
            } else {
              const innovator = {
                idToken: idToken,
                emailAddress: user.email,
                displayName: user.displayName
              } as Innovator;
              this.postInnovator(innovator).pipe(
                tap((postedInnovator: Innovator) => {
                  this.innovator$.next(postedInnovator);
                }),
                catchError((error: any) => {
                  console.log(error);
                  return of(null);
                }),
                takeUntil(this._destroyed$)
              ).subscribe();
            }
          });
        }),
        catchError((error: any) => {
          console.log(error);
          return of(null);
        }),
        takeUntil(this._destroyed$)
      ).subscribe();
    } else {
      this.innovator$.next(null);
    }
  }

  private initializeGoogleApi(): Promise<any> {
    return new Promise((resolve, reject) => {
      this._ngZone.run(() => {
        gapi.client.init({
          apiKey: 'AIzaSyDSq3qOHBHL9YFahSGB5pO3koCIMUgTtuM',
          discoveryDocs: [],
          clientId: '809333411313-ig8j262oip33aj9in335obcf90da4all.apps.googleusercontent.com',
          scope: 'profile email openid'
        }).then((response: any) => {
          console.log("Google API Initialized");
        }, (reason: any) => {
          console.log("Google API Not Initialized");
          console.log(reason);
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
          return this.postInnovator({emailAddress: emailAddress} as Innovator).toPromise();
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

  public signInWithGoogle(store?: {key: string, data: string}): void {
    this.logOut();
    if (store) {
      this._sessionStorageService.storeData(store.key, store.data);
    }
    firebase.auth().signInWithRedirect(this._googleAuthProvider);
  }

  public logOut(): void {
    firebase.auth().signOut();
  }

  public getInnovator(id?: string, emailAddress?: string): Observable<Innovator> {
    let parameters: string;
    if (id) {
      parameters = `getInnovator?id=${encodeURIComponent(id)}`;
    } else if (emailAddress) {
      parameters = `getInnovator?emailAddress=${encodeURIComponent(emailAddress)}`;
    } else {
      return of(null);
    }
    return this._http.get<Innovator>(`${this._serviceURL}${parameters}`);
  }

  public postInnovator(innovator: Innovator) {
    if (this.innovator$.value) {
      innovator.idToken = this.innovator$.value.idToken;
    }
    return this._http.post<Innovator>(`${this._serviceURL}postInnovator`, innovator).pipe(
      tap((innovator: Innovator) => {
        if (innovator) {
          this.innovator$.next(innovator);
        }
      })
    );
  }

  
}
