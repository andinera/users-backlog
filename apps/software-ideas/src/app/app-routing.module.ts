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
    imports: [
        RouterModule.forRoot(routes)
    ],
    exports: [
        RouterModule
    ]
})
export class AppRoutingModule {};
