import { Component, OnInit, OnDestroy, ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { MatTabChangeEvent, MatTabGroup } from '@angular/material/tabs';

import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html',
  styleUrls: ['./idea.component.css']
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  deleteDisabled = true;
  displayNewImplementationForm = false; 
  selectedIndex: number = null;
  tabLabels = ['Description', 'Implementations', 'Innovator', 'Recommendations'];

  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _router: Router,
    private readonly _route: ActivatedRoute,
    private readonly _formBuilder: FormBuilder,
    public readonly authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    // Gather data from resolver
    this._route.data.pipe(
      first(),
      tap((data: {idea: Idea}) => {
        this.idea = data.idea;
        this.authenticationService.innovator.pipe(
          tap((innovator: Innovator) => {
              this.deleteDisabled = (!innovator || this.idea.summary === 'Software Ideas');
          }),
          catchError((error: any) => {
              console.log(error);
              return of(null);
          }),
          takeUntil(this._destroyed$)
        ).subscribe();
      }),
      catchError((error: any) => {
        console.log(error);
        return of(null);
      }),
      takeUntil(this._destroyed$)
    ).subscribe();

    // Route based on fragment
    this._route.fragment.pipe(
      tap((data: string) => {
        if (data) {
          for (let i = 0; i < this.tabLabels.length; i++) {
            if (data === this.tabLabels[i]) {
              this.selectedIndex = i;
              break;
            }
          }
        }
      }),
      takeUntil(this._destroyed$)
    ).subscribe();
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onSelectedTabChange(event: MatTabChangeEvent) {
    history.replaceState(history.state, '', `${location.pathname}#${event.tab.textLabel}`);
  }

  onNewImplementationClick() {
    this.displayNewImplementationForm = !this.displayNewImplementationForm;
  }

  delete(): void {
    this._ideaService.deleteIdea(this.idea.summary).pipe(
      first(),
      tap((deleted) => {
        this._router.navigateByUrl('/Ideas');
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

}
