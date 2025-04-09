import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html'
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  deleteDisabled = true;
  implementationForm: FormGroup;
  addImplementationDisabled = true;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly ideaService: IdeaService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly authenticationService: AuthenticationService,
    private readonly formBuilder: FormBuilder
  ) { }

  ngOnInit(): void {
    this.implementationForm = this.formBuilder.group({
      source: new FormControl('', [
        Validators.required
      ]),
      name: new FormControl('', [
        Validators.required
      ])
    });

    this.route.data.subscribe((data: {idea: Idea}) => {
      this.idea = data.idea;
      this.authenticationService.innovator.subscribe((innovator: Innovator) => {
        this.deleteDisabled = (JSON.stringify(this.idea.innovator) !== JSON.stringify(innovator));
        this.addImplementationDisabled = (innovator ? false : true);
      });


      // Don't touch!!!
      if (!this.deleteDisabled) {
        this.deleteDisabled = (this.idea.summary === 'Software Ideas');
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
