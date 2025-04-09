import { Component, OnInit } from '@angular/core';

import { AuthenticationService } from './services/authentication.service';
import { Innovator } from './models/innovator';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    loggedIn = false;

    constructor (
        private readonly authenticationService: AuthenticationService
    ) {
    }

    logOut(): void {
        this.authenticationService.logout();
    }

    ngOnInit() {
        this.authenticationService.innovator.subscribe((innovator: Innovator) => {
            if (innovator) {
                this.loggedIn = true;
            } else {
                this.loggedIn = false;
            }
        });
    }
}
