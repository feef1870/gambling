import { Routes } from '@angular/router';
import { GameComponent } from './components/game/game';
import { AdminComponent } from './components/admin/admin';
import { LaborComponent } from './components/labor/labor';

export const routes: Routes = [
  { path: '', component: GameComponent },
  { path: 'labor', component: LaborComponent },
  { path: 'admin', component: AdminComponent },
  { path: '**', redirectTo: '' },
];
