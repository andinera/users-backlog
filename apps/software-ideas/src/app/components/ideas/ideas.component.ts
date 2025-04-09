import { Component, OnInit, OnDestroy } from '@angular/core';
import { tap, takeUntil, catchError, first } from 'rxjs/operators';
import { ReplaySubject, of } from 'rxjs';

import { Idea } from '../../models/idea';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html',
  styleUrls: ['./ideas.component.css']
})
export class IdeasComponent implements OnInit, OnDestroy {
  
  title = 'project-ideas';
  categories = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor (
    private readonly _route: ActivatedRoute
  ) {
  }

  ngOnInit() {

    this._route.data.pipe(
      first(),
      tap((data: {categories: Idea[]}) => {
        this.categories = data.categories;
      }),
      catchError((error: any) => {
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
