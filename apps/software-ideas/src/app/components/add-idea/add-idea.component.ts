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
  
  ideaForm: FormGroup;
    
  private _destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly _ideaService: IdeaService,
    private readonly _formBuilder: FormBuilder,
    private readonly _router: Router) {
  }

  ngOnInit(): void {
    this.ideaForm = this._formBuilder.group({
      summary: new FormControl('', [
        Validators.required
      ]),
      description: ''
    });
  }

  ngOnDestroy(): void {
    this._destroyed$.next(true);
    this._destroyed$.complete();
  }

  onSubmit(ideaForm: FormGroup) {
    const idea: Idea = ideaForm.value;
    this._ideaService.postIdea(idea).pipe(
      first(),
      tap((newIdea: Idea) => {
        if (newIdea) {
          this._router.navigateByUrl('/ideas');
        }
      }),
      takeUntil(this._destroyed$),
      catchError((e: any) => {
        console.log(e);
        return of(null);
      })
    ).subscribe();
  }

}
