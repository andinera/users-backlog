import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';

import { tap, catchError } from 'rxjs/operators';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { IdeaComponent } from './components/idea/idea.component';
import { IdeasComponent } from './components/ideas/ideas.component';
import { NewIdeaComponent } from './components/new-idea/new-idea.component';
import { LostComponent } from './components/lost/lost.component';
import { LoginComponent } from './components/login/login.component';
import { ImplementationComponent } from './components/implementation/implementation.component';
import { InnovatorComponent } from './components/innovator/innovator.component';
import { NewImplementationComponent } from './components/new-implementation/new-implementation.component';
import { ImplementationsComponent } from './components/implementations/implementations.component';
import { URLService } from './services/url.service';
import { InnovatorService } from './services/innovator.service';
import { AccountComponent } from './components/account/account.component';
import { SearchResultsComponent } from './components/search-results/search-results.component';
import { HomepageComponent } from './components/homepage/homepage.component';
import { ProductDialogComponent } from './components/implementation/product-dialog/product-dialog.component';

@NgModule({
  declarations: [
    AppComponent,
    IdeaComponent,
    IdeasComponent,
    NewIdeaComponent,
    LostComponent,
    LoginComponent,
    ImplementationComponent,
    InnovatorComponent,
    AccountComponent,
    NewImplementationComponent,
    ImplementationsComponent,
    SearchResultsComponent,
    HomepageComponent,
    ProductDialogComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatAutocompleteModule,
    MatButtonModule,
    MatCheckboxModule,
    MatDialogModule,
    MatIconModule,
    MatInputModule,
    MatProgressBarModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatSnackBarModule,
    MatFormFieldModule,
    ReactiveFormsModule,
    LayoutModule,
  ],
  providers: [
    {
      provide: APP_INITIALIZER,
      useFactory: appInitFactory,
      deps: [InnovatorService, URLService],
      multi: true
    }
  ],
  entryComponents: [
    ProductDialogComponent
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }

export function appInitFactory(innovatorService: InnovatorService, urlService: URLService) {
  return () => new Promise((resolve) => {
    const observable = innovatorService.innovator$.pipe(
      tap(innovator => {
        if (innovator !== undefined) {
          resolve(true);
          observable.unsubscribe();
        }
      }),
      catchError((error: any) => {
        console.error(error);
        return null;
      })
    ).subscribe();
  });
}
