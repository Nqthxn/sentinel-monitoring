import { Routes } from '@angular/router';
import { ContainerListComponent } from './components/container-list/container-list.component';
import { ContainerDetailsComponent } from './pages/container-details/container-details.component';
import { AuthenticationComponent } from './pages/authentication/authentication.component';

export const routes: Routes = [
  { path: '', redirectTo: '/containers', pathMatch: 'full' },
  { path: 'containers', component: ContainerListComponent },
  { path: 'containers/:id', component: ContainerDetailsComponent },
  { path: 'auth', component: AuthenticationComponent },
  { path: '**', redirectTo: '/containers' }
];
