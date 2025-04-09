import { Component, OnInit, OnDestroy } from '@angular/core';

import { AuthenticationService } from './services/authentication.service';
import { Innovator } from './models/innovator';
import { of, ReplaySubject } from 'rxjs';
import { tap, catchError, takeUntil } from 'rxjs/operators';
import { URLService } from './services/url.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit, OnDestroy {

    loggedIn = false;
    displayLogin = false;
    
    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        private readonly _authenticationService: AuthenticationService,
        private readonly _urlService: URLService // Required for storing URLs before login is routed.
    ) {
    }

    logIn(): void {
        this.displayLogin = !this.displayLogin;
    }

    logOut(): void {
        this._authenticationService.logout();
    }

    ngOnInit() {
        this._authenticationService.innovator.pipe(
            tap((innovator: Innovator) => {
                if (innovator) {
                    this.loggedIn = true;
                } else {
                    this.loggedIn = false;
                }
            }),
            catchError((error: any) => {
                console.log(error);
                return of(null);
            }),
            takeUntil(this._destroyed$)
        ).subscribe();
    }

    ngOnDestroy(): void {
      this._destroyed$.next(true);
      this._destroyed$.complete();
    }
}
