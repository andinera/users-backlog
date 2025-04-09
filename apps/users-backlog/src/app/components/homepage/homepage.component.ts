import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation.model';
import { Idea } from 'src/app/models/idea.model';

@Component({
  selector: 'app-homepage',
  templateUrl: './homepage.component.html',
  styleUrls: ['./homepage.component.css']
})
export class HomepageComponent implements OnInit {
  
  ideas = [];
  implementations = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _route: ActivatedRoute,
    private readonly _snackBar: MatSnackBar
  ) { }

  ngOnInit(): void {

    this._route.data.pipe(
      first(),
      tap(
        (data: {
            ideas: Idea[],
            implementations: Implementation[]
        }) => {
          this.ideas = data.ideas;
          this.implementations = data.implementations;
      }),
      tap((data: {ideas: Idea[]}) => {
        this.ideas = data.ideas;
      }),
      catchError((error: any) => {
        console.error(error);
        this._snackBar.open('Failed to load ideas and/or implementations.', 'Close');
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

}
