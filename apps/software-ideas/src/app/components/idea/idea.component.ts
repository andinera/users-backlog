import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { ReplaySubject, of } from 'rxjs';
import { takeUntil, tap, first, catchError } from 'rxjs/operators';

import { IdeaService } from '../../services/idea.service';
import { Idea } from '../../models/idea';
import { InnovatorService } from 'src/app/services/innovator.service';
import { Implementation } from 'src/app/models/implementation';
import { ImplementationService } from 'src/app/services/implementation.service';

@Component({
  selector: 'app-idea',
  templateUrl: './idea.component.html'
})
export class IdeaComponent implements OnInit, OnDestroy {

  idea: Idea;
  deleteDisabled: boolean;
  implementationForm: FormGroup;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly ideaService: IdeaService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    public readonly innovatorService: InnovatorService,
    private readonly formBuilder: FormBuilder,
    private readonly implementationService: ImplementationService
  ) { }

  ngOnInit(): void {
    this.implementationForm = this.formBuilder.group({
      source: new FormControl(''),
      name: new FormControl('', [
        Validators.required
      ])
    });

    this.route.data.subscribe((data: {idea: Idea}) => {
      this.idea = data.idea;
      this.deleteDisabled = (JSON.stringify(this.idea.innovator) !== JSON.stringify(this.innovatorService.innovator));


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

  addImplementation(form: any): void {
    const implementation: Implementation = form.value;
    implementation.implementer = this.innovatorService.innovator;
    implementation.idea = this.idea;
    this.implementationService.postImplementation(implementation).pipe(
      first(),
      tap((implementation: Implementation) => {
        if (implementation) {
          this.idea.implementations.push(implementation);
        }
      }),
      takeUntil(this.destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

}
