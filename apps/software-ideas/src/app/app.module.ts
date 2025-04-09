import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { RouterModule, Routes } from '@angular/router';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppComponent } from './app.component';
import { IdeaComponent } from './components/idea/idea.component';
import { IdeasComponent } from './components/ideas/ideas.component';
import { AddIdeaComponent } from './components/add-idea/add-idea.component';
import { AuthGuard } from './guards/auth.guard';
import { IdeaResolver } from './resolvers/idea.resolver';
import { LostComponent } from './components/lost/lost.component';
import { LoginComponent } from './components/login/login.component';
import { ImplementationComponent } from './components/implementation/implementation.component';
import { ImplementationResolver } from './resolvers/implementation.resolver';
import { InnovatorComponent } from './components/innovator/innovator.component';
import { InnovatorResolver } from './resolvers/innovator.resolver';

const routes: Routes = [
  {
    path: 'ideas',
    component: IdeasComponent
  },
  {
    path: 'idea/:summary',
    component: IdeaComponent,
    resolve: {
      idea: IdeaResolver
    }
  },
  {
    path: 'implementation/:name',
    component: ImplementationComponent,
    resolve: {
      implementation: ImplementationResolver
    }
  },
  {
    path: 'innovator/:emailAddress',
    component: InnovatorComponent,
    resolve: {
      innovator: InnovatorResolver
    }
  },
  {
    path: 'addIdea',
    component: AddIdeaComponent,
    canActivate: [AuthGuard]
  },
  {
    path: 'login',
    component: LoginComponent
  },
  {
    path: '',
    redirectTo: 'ideas',
    pathMatch: 'full'
  },
  {
    path: '**',
    component: LostComponent
  }
];

@NgModule({
  declarations: [
    AppComponent,
    IdeaComponent,
    IdeasComponent,
    AddIdeaComponent,
    LostComponent,
    LoginComponent,
    ImplementationComponent,
    InnovatorComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatToolbarModule,
    ReactiveFormsModule,
    RouterModule.forRoot(routes)
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
