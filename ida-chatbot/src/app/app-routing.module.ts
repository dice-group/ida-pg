import {NgModule} from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {UserComponent} from './components/user/user.component';
import {LoginComponent} from './components/login/login.component';
import {SignupComponent} from './components/signup/signup.component';
import {HomeComponent} from './components/home/home.component';
const routes: Routes = [
    { path: 'user', component: UserComponent },
    { path: 'login', component: LoginComponent },
    { path: 'signup', component: SignupComponent },
    { path: '', component: HomeComponent },
    { path: '**', redirectTo: '' }
];
@NgModule({
    imports: [
        RouterModule.forRoot(routes)
    ],
    exports: [
        RouterModule
    ],
    declarations: []
})
export class AppRoutingModule { }
