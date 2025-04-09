import { Injectable, NgZone, OnDestroy } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of, ReplaySubject, BehaviorSubject, from } from 'rxjs';
import { tap, catchError, takeUntil, mergeMap } from 'rxjs/operators';

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
    private readonly _sessionStorageService: SessionStorageService,
    private readonly _snackBar: MatSnackBar
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
          if (innovator) {
            this.innovator$.next(innovator);
          } else {
            const innovator = {
              emailAddress: user.email,
              displayName: user.displayName
            } as Innovator;
            this.postInnovator(innovator).pipe(
              tap((postedInnovator: Innovator) => {
                this.innovator$.next(postedInnovator);
              }),
              catchError((error: any) => {
                console.error(error);
                this._snackBar.open('Failed to post innovator.', 'Close');
                return of(null);
              }),
              takeUntil(this._destroyed$)
            ).subscribe();
          }
        }),
        catchError((error: any) => {
          console.error(error);
          this._snackBar.open('Failed to load innovator.', 'Close');
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
          console.error("Google API Not Initialized");
          console.error(reason);
          this._snackBar.open('Failed to initialize Google API.', 'Close');
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
          console.error(error);
          this._snackBar.open('Failed to create user.', 'Close');
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
            this._snackBar.open('Unknown credentials.', 'Close');
            reject("Unknown credentials.");
          } else if (!credential.user.emailVerified) {
            this._snackBar.open('Email address has not been verified.', 'Close');
            reject("Email address has not been verified.");
          }
          resolve("");
        }).catch(error => {
          console.error(error);
          this._snackBar.open('Invalid email address or password.', 'Close');
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

  public getIdToken(): Observable<string> {
    return from(firebase.auth().currentUser.getIdToken()) as Observable<string>;
  }

  public getInnovator(id?: string, emailAddress?: string): Observable<Innovator> {
    let params = new HttpParams();
    if (id) {
      params = params.set('id', id);
    } else if (emailAddress) {
      params = params.set('emailAddress', emailAddress);
    } else {
      return of(null);
    }
    const options = {params: params};
    return this._http.get<Innovator>(`${this._serviceURL}getInnovator`, options);
  }

  public postInnovator(innovator: Innovator): Observable<Innovator> {
    return this.getIdToken().pipe(
      mergeMap((idToken: string) => {
        innovator.idToken = idToken;
        return this._http.post<Innovator>(`${this._serviceURL}postInnovator`, innovator).pipe(
          tap((innovator: Innovator) => {
            if (innovator) {
              this.innovator$.next(innovator);
            }
          })
        );
      })
    );
  }

  
}
