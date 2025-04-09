import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivationEnd } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { of, ReplaySubject } from 'rxjs';
import { tap, catchError, takeUntil, filter } from 'rxjs/operators';

import { AuthenticationService } from './services/authentication.service';
import { Innovator } from './models/innovator.model';
import { URLService } from './services/url.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

    loggedIn = false;
    
    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        public readonly authenticationService: AuthenticationService,
        private readonly _urlService: URLService, // Required for storing URLs.
        private readonly _router: Router
    ) {
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
      this._destroyed$.next(true);
      this._destroyed$.complete();
    }

    public logOut(): void {
        this.authenticationService.logOut();
    }
}
