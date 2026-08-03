import { Component, inject, signal } from '@angular/core';
import { GameService } from '../../services/game';
import { GameResponse } from '../../models/types';
import { delay } from 'rxjs';

@Component({
  selector: 'app-game',
  imports: [],
  templateUrl: './game.html',
  styleUrl: './game.css',
})
export class GameComponent {
  private gameService = inject(GameService);

  gameState = signal<GameResponse | null>(null);
  betAmount = signal<number>(100);
  isLoading = signal<boolean>(false);
  loadingMessage = signal<string>('');

  updateBet(event: Event) {
    const input = event.target as HTMLInputElement;
    this.betAmount.set(Number(input.value));
  }

  startGame() {
    this.isLoading.set(true);
    this.loadingMessage.set('Shuffling deck...');

    this.gameService.startGame(this.betAmount())
      .pipe(delay(600))
      .subscribe({
      next: (res) => {
        this.gameState.set(res);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error('Failed to start game', err);
        this.isLoading.set(false);
      },
    });
  }

  action(type: string) {
    const gameId = this.gameState()?.id;
    if (!gameId) return;

    this.isLoading.set(true);

    if (type === 'STAND') {
      this.loadingMessage.set("Dealer is playing...");
    } else {
      this.loadingMessage.set("Dealing card...");
    }

    const delayTime = type === 'STAND' ? 1500 : 500;

    this.gameService.processAction(gameId, type)
      .pipe(delay(delayTime))
      .subscribe({
      next: (res) => {
        this.gameState.set(res);
        this.isLoading.set(false);
      },
      error: (err) => {
        console.error(`Failed to process ${type}`, err);
        this.isLoading.set(false);
      },
    });
  }

  getSuitSymbol(suit: string): string {
    const symbols: { [key: string]: string } = {
      HEARTS: '♥',
      DIAMONDS: '♦',
      CLUBS: '♣',
      SPADES: '♠',
    };
    return symbols[suit] || '?';
  }

  getRankSymbol(rank: string): string {
    const ranks: { [key: string]: string } = {
      ACE: 'A',
      TWO: '2',
      THREE: '3',
      FOUR: '4',
      FIVE: '5',
      SIX: '6',
      SEVEN: '7',
      EIGHT: '8',
      NINE: '9',
      TEN: '10',
      JACK: 'J',
      QUEEN: 'Q',
      KING: 'K',
    };
    return ranks[rank] || rank;
  }
}
