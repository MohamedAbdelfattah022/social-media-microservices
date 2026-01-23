import { Routes } from '@angular/router';
import { Login } from './features/login/login';
import { Signup } from './features/signup/signup';
import { Home } from './features/home/home';
import { LandingPage } from './features/landing-page/landing-page';
import { authGuard, guestGuard } from './core/guards/auth-guard';
import { UserProfile } from './features/user-profile/user-profile';



export const routes: Routes = [
  { path: 'login', component: Login, canActivate: [guestGuard] },
  { path: 'signup', component: Signup, canActivate: [guestGuard] },
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: '', component: LandingPage, canActivate: [guestGuard] },
  { path: 'profile/:userId', component: UserProfile, canActivate: [authGuard] }
];
