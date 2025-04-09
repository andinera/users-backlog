import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation.model';

@Component({
  selector: 'app-implementations',
  templateUrl: './implementations.component.html',
  styleUrls: ['./implementations.component.css']
})
export class ImplementationsComponent implements OnInit {
  
  implementations = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _route: ActivatedRoute,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {

    this._route.data.pipe(
      first(),
      tap((data: {implementations: Implementation[]}) => {
        this.implementations = data.implementations;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load implementations.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

}
