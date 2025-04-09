import { Component, OnInit, OnDestroy } from '@angular/core';
import { tap, takeUntil, catchError } from 'rxjs/operators';

import { Idea } from '../../models/idea';
import { IdeaService } from '../../services/idea.service';
import { ReplaySubject, of } from 'rxjs';

@Component({
  selector: 'app-ideas',
  templateUrl: './ideas.component.html'
})
export class IdeasComponent implements OnInit, OnDestroy {
    
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  
  title = 'project-ideas';
  count = 0;
  ideas: Idea[];


  constructor (private readonly ideaService: IdeaService) {
  }

  ngOnInit() {
    this.ideaService.getAllIdeas().pipe(
      tap((ideas: Idea[]) => {
        this.ideas = ideas;
      }),
      takeUntil(this.destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }
}
