import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Idea } from 'src/app/models/idea.model';

@Component({
  selector: 'app-implementations',
  templateUrl: './implementations.component.html',
  styleUrls: ['./implementations.component.css']
})
export class ImplementationsComponent implements OnInit {
  
  categories = [];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _route: ActivatedRoute
  ) { }

  ngOnInit(): void {

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

}
