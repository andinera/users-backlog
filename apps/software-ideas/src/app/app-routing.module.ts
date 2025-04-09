import { NgModule } from "@angular/core";
import { Routes, RouterModule, ExtraOptions } from '@angular/router';

import { IdeasComponent } from './components/ideas/ideas.component';
import { IdeaComponent } from './components/idea/idea.component';
import { IdeaResolver } from './resolvers/idea.resolver';
import { ImplementationComponent } from './components/implementation/implementation.component';
import { ImplementationResolver } from './resolvers/implementation.resolver';
import { InnovatorComponent } from './components/innovator/innovator.component';
import { InnovatorResolver } from './resolvers/innovator.resolver';
import { NewIdeaComponent } from './components/new-idea/new-idea.component';
import { LostComponent } from './components/lost/lost.component';
import { CategoriesResolver } from './resolvers/categories.resolver';
import { IdeasResolver } from './resolvers/ideas.resolver';
import { LoginComponent } from './components/login/login.component';
import { ImplementationsComponent } from './components/implementations/implementations.component';
import { ImplementationsResolver } from './resolvers/implementations.resolver';
import { NewImplementationComponent } from './components/new-implementation/new-implementation.component';
import { LogInGuard } from './guards/log-in.guard';
import { RedirectGuard } from './guards/redirect.guard';


const extraOptions = {
    // enableTracing: true
} as ExtraOptions;

const routes: Routes = [
    {
        path: 'Ideas',
        component: IdeasComponent,
        resolve: {
            categories: IdeasResolver
        }
    },
    {
        path: 'Idea/:summary',
        component: IdeaComponent,
        resolve: {
            idea: IdeaResolver,
            categories: CategoriesResolver
        }
    },
    {
        path: 'Implementations',
        component: ImplementationsComponent,
        resolve: {
            implementations: ImplementationsResolver
        }
    },
    {
        path: 'Implementation/:name',
        component: ImplementationComponent,
        resolve: {
            implementation: ImplementationResolver
        }
    },
    {
        path: 'Innovator/:id',
        component: InnovatorComponent,
        resolve: {
            innovator: InnovatorResolver
        }
    },
    {
        path: 'New Idea',
        component: NewIdeaComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'Edit Idea/:summary',
        component: NewIdeaComponent,
        resolve: {
            idea: IdeaResolver,
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'New Implementation',
        component: NewImplementationComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'Edit Implementation',
        component: NewImplementationComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'Log In',
        component: LoginComponent,
        canActivate: [RedirectGuard]
    },
    {
        path: 'Lost',
        component: LostComponent
    },
    {
        path: '',
        redirectTo: 'Implementations',
        pathMatch: 'full'
    },
    {
        path: '**',
        redirectTo: 'Lost',
        pathMatch: 'full'
    }
];

@NgModule({
    imports: [
        RouterModule.forRoot(routes, extraOptions)
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule {};
