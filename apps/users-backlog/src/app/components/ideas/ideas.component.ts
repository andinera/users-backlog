import { Component, OnInit, OnDestroy } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { tap, takeUntil, catchError, first } from 'rxjs/operators';
import { ReplaySubject, of } from 'rxjs';

import { Idea } from '../../models/idea.model';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html',
  styleUrls: ['./ideas.component.css']
})
export class IdeasComponent implements OnInit, OnDestroy {
  
  ideas = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor (
    private readonly _route: ActivatedRoute,
    private readonly _snackBar: MatSnackBar
  ) {
  }

  ngOnInit() {

    this._route.data.pipe(
      first(),
      tap((data: {ideas: Idea[]}) => {
        this.ideas = data.ideas;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load ideas.', 'Close');
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
