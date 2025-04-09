import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { of, ReplaySubject } from 'rxjs';
import { first, tap, catchError, takeUntil } from 'rxjs/operators';

import { Innovator } from 'src/app/models/innovator.model';

@Component({
  selector: 'app-innovator',
  templateUrl: './innovator.component.html',
  styleUrls: ['./innovator.component.css']
})
export class InnovatorComponent implements OnInit, OnDestroy {

  innovator: Innovator;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _route: ActivatedRoute
  ) { }

  ngOnInit(): void {
    this._route.data.pipe(
      first(),
      tap((data: {innovator: Innovator}) => {
        this.innovator = data.innovator;
      }),
      catchError((error: any) => {
        console.log(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe()
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

}
