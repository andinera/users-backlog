import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatDialogModule } from '@angular/material/dialog';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatTabsModule } from '@angular/material/tabs';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatTooltipModule } from '@angular/material/tooltip';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ClarityModule } from '@clr/angular';
import { LayoutModule } from '@angular/cdk/layout';
import { MatSidenavModule } from '@angular/material/sidenav';

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
    NewImplementationComponent,
    RecommendationComponent,
    ImplementationsComponent
  ],
  imports: [
    AppRoutingModule,
    BrowserModule,
    BrowserAnimationsModule,
    ClarityModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCardModule,
    MatDialogModule,
    MatExpansionModule,
    MatIconModule,
    MatInputModule,
    MatListModule,
    MatMenuModule,
    MatSelectModule,
    MatSidenavModule,
    MatTabsModule,
    MatToolbarModule,
    MatTooltipModule,
    ReactiveFormsModule,
    LayoutModule,
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
