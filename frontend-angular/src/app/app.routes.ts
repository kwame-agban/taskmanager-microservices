import { Routes } from '@angular/router';
import { Login } from './pages/login/login';
import { Tasks } from './pages/tasks/tasks';
import { authGuard } from './core/auth.guard';

export const routes: Routes = [
  { path: 'login', component: Login },
  { path: 'tasks', component: Tasks, canActivate: [authGuard] },
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: '**', redirectTo: 'login' }
];
