import { Routes } from '@angular/router';
import { GameComponent } from './components/game/game';
import { AdminComponent } from './components/admin/admin';

export const routes: Routes = [
  { path: '', component: GameComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: '' },
];
