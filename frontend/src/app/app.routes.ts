import { Routes } from '@angular/router';
import { GameComponent } from './components/game/game';

export const routes: Routes = [
  { path: '', component: GameComponent },
  { path: '**', redirectTo: '' },
];
