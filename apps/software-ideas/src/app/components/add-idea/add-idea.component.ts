import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { tap, catchError, first, takeUntil } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { IdeaService } from 'src/app/services/idea.service';
import { Idea } from 'src/app/models/idea';

@Component({
  selector: 'app-add-idea',
  templateUrl: './add-idea.component.html'
})
export class AddIdeaComponent implements OnInit, OnDestroy {
    
  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);
  
  ideaForm: FormGroup;

  constructor(
    private readonly ideaService: IdeaService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router) {
  }

  ngOnInit(): void {
    this.ideaForm = this.formBuilder.group({
      summary: new FormControl('', [
        Validators.required
      ]),
      description: ''
    });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  onSubmit(ideaForm: FormGroup) {
    const idea: Idea = ideaForm.value;
    this.ideaService.postIdea(idea).pipe(
      first(),
      tap((idea: Idea) => {
        if (idea) {
          this.router.navigateByUrl('/ideas');
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
