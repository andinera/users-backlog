import { BrowserModule } from '@angular/platform-browser';
import { NgModule, APP_INITIALIZER } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LayoutModule } from '@angular/cdk/layout';

import { tap } from 'rxjs/operators';

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
import { RecommendationComponent } from './components/recommendation/recommendation.component';
import { ImplementationsComponent } from './components/implementations/implementations.component';
import { URLService } from './services/url.service';
import { InnovatorService } from './services/innovator.service';
import { AccountComponent } from './components/account/account.component';

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
    RecommendationComponent,
    ImplementationsComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCheckboxModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
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
      })
    ).subscribe();
  });
}
