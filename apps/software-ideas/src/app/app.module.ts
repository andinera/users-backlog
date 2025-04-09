import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatInputModule } from '@angular/material/input';
import { MatMenuModule } from '@angular/material/menu';
import { MatSelectModule } from '@angular/material/select';
import { MatToolbarModule } from '@angular/material/toolbar';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { IdeaComponent } from './components/idea/idea.component';
import { IdeasComponent } from './components/ideas/ideas.component';
import { AddIdeaComponent } from './components/add-idea/add-idea.component';
import { LostComponent } from './components/lost/lost.component';
import { LoginComponent } from './components/login/login.component';
import { ImplementationComponent } from './components/implementation/implementation.component';
import { InnovatorComponent } from './components/innovator/innovator.component';
import { AddImplementationComponent } from './components/add-implementation/add-implementation.component';
import { CategoryComponent } from './components/category/category.component';

@NgModule({
  declarations: [
    AppComponent,
    IdeaComponent,
    IdeasComponent,
    AddIdeaComponent,
    LostComponent,
    LoginComponent,
    ImplementationComponent,
    InnovatorComponent,
    AddImplementationComponent,
    CategoryComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    MatButtonModule,
    MatCardModule,
    MatExpansionModule,
    MatInputModule,
    MatMenuModule,
    MatSelectModule,
    MatToolbarModule,
    ReactiveFormsModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
