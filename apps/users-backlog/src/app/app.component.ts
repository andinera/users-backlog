import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder } from '@angular/forms';
import { ReplaySubject } from 'rxjs';
import { tap } from 'rxjs/operators';

import { InnovatorService } from './services/innovator.service';
import { SearchForm } from './models/forms/search.form';
import { SearchService } from './services/search.service';
import { Model } from './models/model.model';
import { Router } from '@angular/router';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit, OnDestroy {
    
    public searchForm: FormGroup;

    private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

    constructor (
        public readonly innovatorService: InnovatorService,
        private readonly _formBuilder: FormBuilder,
        private readonly _router: Router
    ) {
        this.searchForm = this._formBuilder.group(new SearchForm());
    }

    ngOnInit() {
    }

    ngOnDestroy(): void {
      this._destroyed$.next(true);
      this._destroyed$.complete();
    }

    public onSearch(): void {
        this._router.navigate([`/search/${this.searchForm.controls.search.value}`]);
      }

    public logOut(): void {
        this.innovatorService.logOut();
    }
}
