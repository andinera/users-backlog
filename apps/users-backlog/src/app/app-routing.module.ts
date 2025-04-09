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
import { AccountComponent } from './components/account/account.component';
import { SearchResultsComponent } from './components/search-results/search-results.component';
import { SearchResolver } from './resolvers/search.resolver';


const extraOptions = {
    // enableTracing: true
} as ExtraOptions;

const routes: Routes = [
    {
        path: 'ideas',
        component: IdeasComponent,
        resolve: {
            categories: IdeasResolver
        }
    },
    {
        path: 'idea/:summary',
        component: IdeaComponent,
        resolve: {
            idea: IdeaResolver,
            categories: CategoriesResolver
        }
    },
    {
        path: 'implementations',
        component: ImplementationsComponent,
        resolve: {
            implementations: ImplementationsResolver
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
        path: 'search/:criteria',
        component: SearchResultsComponent,
        resolve: {
            searchResults: SearchResolver
        }
    },
    {
        path: 'innovator/:id',
        component: InnovatorComponent,
        resolve: {
            innovator: InnovatorResolver
        }
    },
    {
        path: 'account',
        component: AccountComponent,
        canActivate: [LogInGuard]
    },
    {
        path: 'new-idea',
        component: NewIdeaComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'edit-idea/:summary',
        component: NewIdeaComponent,
        resolve: {
            idea: IdeaResolver,
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'new-implementation',
        component: NewImplementationComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'edit-implementation',
        component: NewImplementationComponent,
        resolve: {
            categories: CategoriesResolver
        },
        canActivate: [LogInGuard]
    },
    {
        path: 'log-in',
        component: LoginComponent,
        canActivate: [RedirectGuard]
    },
    {
        path: 'lost',
        component: LostComponent
    },
    {
        path: '',
        redirectTo: 'implementations',
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
        RouterModule.forRoot(routes, extraOptions)
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule {};
