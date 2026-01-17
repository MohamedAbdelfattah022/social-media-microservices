import { Routes } from '@angular/router';
import { Login } from './features/login/login';
import { Signup } from './features/signup/signup';
import { Home } from './features/home/home';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'signup', component: Signup },
  { path: '', component: Home }
];
