import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { GameResponse } from '../models/types';

@Injectable({
  providedIn: 'root',
})
export class GameService {
  private http = inject(HttpClient);
  private apiUrl = 'http://localhost:8080/api/games';

  startGame(betAmount: number): Observable<GameResponse> {
    return this.http.post<GameResponse>(this.apiUrl, { betAmount });
  }

  processAction(gameId: number, action: string): Observable<GameResponse> {
    return this.http.post<GameResponse>(`${this.apiUrl}/${gameId}/action`, { action });
  }
}
