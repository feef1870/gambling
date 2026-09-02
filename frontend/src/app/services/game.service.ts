import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameAction, GameResponse } from '../models/types';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.apiUrl}/api/games`;

  startGame(betAmount: number): Observable<GameResponse> {
    return this.http.post<GameResponse>(this.apiUrl, { betAmount });
  }

  processAction(gameId: number, action: GameAction): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.apiUrl}/${gameId}/action`, { action });
  }
}
