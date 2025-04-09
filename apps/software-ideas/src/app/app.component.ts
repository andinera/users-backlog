import { Component, OnInit, OnDestroy } from '@angular/core';

import { AuthenticationService } from './services/authentication.service';
import { Innovator } from './models/innovator';
import { of, ReplaySubject } from 'rxjs';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit, OnDestroy {

    loggedIn = false;
    displayLogin = false;
    
    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        private readonly _authenticationService: AuthenticationService
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
