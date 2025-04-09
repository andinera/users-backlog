import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivationEnd } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of, ReplaySubject } from 'rxjs';
import { tap, catchError, takeUntil, filter } from 'rxjs/operators';

import { AuthenticationService } from './services/authentication.service';
import { Innovator } from './models/innovator';
import { URLService } from './services/url.service';
import { LoginComponent } from './components/login/login.component';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

    loggedIn = false;
    userName = 'guest';
    equalityContent = '';
    contentType = '';
    
    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        public readonly authenticationService: AuthenticationService,
        private readonly _urlService: URLService, // Required for storing URLs.
        private readonly _router: Router,
        private readonly _loginDialog: MatDialog
    ) {
    }

    ngOnInit() {
        this.authenticationService.innovator.pipe(
            tap((innovator: Innovator) => {
                this.loggedIn = (!!innovator);
                if (innovator) {
                    this.userName = innovator.emailAddress;
                } else {
                    this.userName = 'guest';
                }
            }),
            catchError((error: any) => {
                console.log(error);
                return of(null);
            }),
            takeUntil(this._destroyed$)
        ).subscribe();

        this._router.events.pipe(
            filter((event: any) => event instanceof ActivationEnd),
            tap((event: ActivationEnd) => {
                let urlSegments = this._router.url.split('#')[0].split('/');
                if (urlSegments.length <= 2) {
                    this.equalityContent = '';
                    this.contentType = decodeURI(urlSegments.pop());
                } else {
                    this.equalityContent = decodeURI(urlSegments.pop());
                    this.contentType = decodeURI(urlSegments.pop());
                }
            })
        ).subscribe();
    }

    ngOnDestroy(): void {
      this._destroyed$.next(true);
      this._destroyed$.complete();
    }

    login(): void {
        this._loginDialog.open(LoginComponent);
    }

    logout(): void {
        this.authenticationService.logout();
    }
}
