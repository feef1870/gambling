import { Routes } from '@angular/router';
import { GameComponent } from './components/game/game.component';
import { AdminComponent } from './components/admin/admin.component';
import { LaborComponent } from './components/labor/labor.component';
import { adminGuard } from './guards/admin.guard';

export const routes: Routes = [
  { path: '', component: GameComponent },
  { path: 'labor', component: LaborComponent },
  { path: 'admin', component: AdminComponent, canActivate: [adminGuard] },
  { path: '**', redirectTo: '' },
];
