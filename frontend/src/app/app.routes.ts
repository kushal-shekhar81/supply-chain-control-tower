import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    // Lazy loading: the dashboard's code downloads only when this route is visited.
    loadComponent: () => import('./dashboard/dashboard').then((m) => m.Dashboard),
    title: 'Control Tower',
  },
  { path: '**', redirectTo: '' },
];
