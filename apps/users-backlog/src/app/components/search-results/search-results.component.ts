import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
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
    private readonly _route: ActivatedRoute,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {

    this._route.data.pipe(
      tap((data: {searchResults: SearchResults[]}) => {
        this.implementations = data.searchResults.filter(sr => sr.className === 'implementation');
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load search results.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

}
