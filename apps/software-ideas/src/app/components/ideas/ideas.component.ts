import { Component, OnInit, OnDestroy } from '@angular/core';
import { tap, takeUntil, catchError, first } from 'rxjs/operators';

import { Idea } from '../../models/idea';
import { IdeaService } from '../../services/idea.service';
import { ReplaySubject, of } from 'rxjs';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html'
})
export class IdeasComponent implements OnInit, OnDestroy {
  
  title = 'project-ideas';
  count = 0;
  ideas: Idea[];
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor (private readonly _ideaService: IdeaService) {
  }

  ngOnInit() {
    this._ideaService.getAllIdeas().pipe(
      first(),
      tap((ideas: Idea[]) => {
        this.ideas = ideas;
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }
}
