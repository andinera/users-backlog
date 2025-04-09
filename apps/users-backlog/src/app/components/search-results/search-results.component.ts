import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { SearchResults } from 'src/app/models/search-results.model';

@Component({
  selector: 'app-search-results',
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.css']
})
export class SearchResultsComponent implements OnInit {
  
  implementations = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _route: ActivatedRoute
  ) { }

  ngOnInit(): void {

    this._route.data.pipe(
      tap((data: {searchResults: SearchResults[]}) => {
        this.implementations = data.searchResults.filter(sr => sr.className === 'implementation');
      }),
      catchError((error: any) => {
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

}
