import { Component, OnInit } from '@angular/core';

import { URLService } from './services/url.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html'
})
export class AppComponent implements OnInit {

    constructor (
        private readonly urlService: URLService
    ) {
    }

    ngOnInit() {
    }
}
