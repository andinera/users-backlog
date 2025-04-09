import { NgModule } from "@angular/core";
import { Routes, RouterModule } from '@angular/router';

import { IdeasComponent } from './components/ideas/ideas.component';
import { IdeaComponent } from './components/idea/idea.component';
import { IdeaResolver } from './resolvers/idea.resolver';
import { ImplementationComponent } from './components/implementation/implementation.component';
import { ImplementationResolver } from './resolvers/implementation.resolver';
import { InnovatorComponent } from './components/innovator/innovator.component';
import { InnovatorResolver } from './resolvers/innovator.resolver';
import { AddIdeaComponent } from './components/add-idea/add-idea.component';
import { AuthGuard } from './guards/auth.guard';
import { LoginComponent } from './components/login/login.component';
import { LostComponent } from './components/lost/lost.component';
import { CategoryComponent } from './components/category/category.component';
import { CategoryResolver } from './resolvers/category.resolver';
import { CategoriesResolver } from './resolvers/categories.resolver';


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
        canActivate: [AuthGuard],
        resolve: {
            categories: CategoriesResolver
        }
    },
    {
        path: 'category/:categoryName',
        component: CategoryComponent,
        resolve: {
            ideas: CategoryResolver
        }
    },
    {
        path: 'login',
        component: LoginComponent,
    },
    {
        path: 'lost',
        component: LostComponent
    },
    {
        path: '',
        redirectTo: 'ideas',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: 'lost',
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule {};
