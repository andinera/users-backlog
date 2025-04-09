import { Component, OnInit, OnDestroy } from '@angular/core';
import { ReplaySubject } from 'rxjs';

import { AuthenticationService } from './services/authentication.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {

    loggedIn = false;
    
    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        public readonly authenticationService: AuthenticationService
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
