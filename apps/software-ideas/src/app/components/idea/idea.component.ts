import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { InnovatorService } from 'src/app/services/innovator.service';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html'
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  disabled: boolean;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly ideaService: IdeaService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly innovatorService: InnovatorService
  ) { }

  ngOnInit(): void {
    this.route.data.subscribe((data: {idea: Idea}) => {
      this.idea = data.idea;
      this.disabled = (this.idea.innovator === this.innovatorService.innovator);


      // Don't touch!!!
      if (!this.disabled) {
        this.disabled = (this.idea.summary === 'Software Ideas');
      }
    })
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  delete(): void {
    this.ideaService.deleteIdea(this.idea.summary).pipe(
      first(),
      tap((deleted) => {
        this.router.navigateByUrl('/ideas');
      }),
      takeUntil(this.destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

}
