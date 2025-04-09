import { Component, OnInit, Input } from '@angular/core';
import { FormGroup, FormBuilder, FormControl, Validators } from '@angular/forms';
import { first, tap, takeUntil, catchError } from 'rxjs/operators';
import { of, ReplaySubject } from 'rxjs';

import { Implementation } from 'src/app/models/implementation';
import { ImplementationService } from 'src/app/services/implementation.service';
import { Idea } from 'src/app/models/idea';
import { AuthenticationService } from 'src/app/services/authentication.service';
import { Innovator } from 'src/app/models/innovator';

@Component({
  selector: 'app-add-implementation',
  templateUrl: './add-implementation.component.html'
})
export class AddImplementationComponent implements OnInit {

  @Input() idea: Idea;

  implementationForm: FormGroup;

  private destroyed$: ReplaySubject<boolean> = new ReplaySubject(1);

  constructor(
    private readonly formBuilder: FormBuilder,
    private readonly implementationService: ImplementationService,
    private readonly authenticationService: AuthenticationService
  ) { }

  ngOnInit(): void {
    this.implementationForm = this.formBuilder.group({
      source: new FormControl(''),
      name: new FormControl('', [
        Validators.required
      ])
    });
  }

  ngOnDestroy(): void {
    this.destroyed$.next(true);
    this.destroyed$.complete();
  }

  addImplementation(form: any): void {
    const implementation: Implementation = form.value;
    this.authenticationService.innovator.pipe(
      tap((innovator: Innovator) => {
        implementation.implementer = innovator;
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
      })
    );
  }

}
