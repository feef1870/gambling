import { Component, inject, signal } from '@angular/core';
import { GameService } from '../../services/game.service';
import { GameResponse } from '../../models/types';
import { delay } from 'rxjs';
import { UserService } from '../../services/user.service';

@Component({
  selector: 'app-game',
  imports: [],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent {
  private gameService = inject(GameService);
  private userService = inject(UserService);

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

    this.gameService
      .startGame(this.betAmount())
      .pipe(delay(600))
      .subscribe({
        next: (res) => {
          this.gameState.set(res);
          this.isLoading.set(false);
          this.userService.refreshUser();
        },
        error: (err) => {
          console.error('Failed to start game', err);
          this.isLoading.set(false);
        },
      });
  }

  private wait(ms: number) {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }

  action(type: string) {
    const gameId = this.gameState()?.id;
    if (!gameId) return;

    this.isLoading.set(true);
    this.loadingMessage.set(type === 'STAND' ? 'Dealer is playing...' : 'Dealing card...');

    this.gameService.processAction(gameId, type).subscribe({
      next: async (res) => {
        if (res.status !== 'IN_PROGRESS') {
          await this.animateDealerTurn(res);
        } else {
          this.gameState.set(res);
          this.isLoading.set(false);
          this.userService.refreshUser();
        }
      },
      error: (err) => {
        console.error(`Failed to process ${type}`, err);
        this.isLoading.set(false);
      },
    });
  }

  async animateDealerTurn(res: GameResponse) {
    const finalHand = res.dealerHand;

    const visibleHand = finalHand.slice(0, 2);

    this.gameState.set({
      ...res,
      dealerHand: [...visibleHand],
      dealerTotal: null,
      status: 'IN_PROGRESS',
    });

    for (let i = 2; i < finalHand.length; i++) {
      await this.wait(1000);
      visibleHand.push(finalHand[i]);

      this.gameState.set({
        ...res,
        dealerHand: [...visibleHand],
        dealerTotal: null,
        status: 'IN_PROGRESS',
      });
    }

    await this.wait(800);

    this.gameState.set(res);
    this.isLoading.set(false);
    this.userService.refreshUser();
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
