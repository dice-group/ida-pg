import {NgModule} from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {StoryboardComponent} from './components/storyboard/storyboard.component';

const routes: Routes = [
  { path: 'getstory', component: StoryboardComponent },
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
