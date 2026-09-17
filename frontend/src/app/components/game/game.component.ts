import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { GameService } from '../../services/game.service';
import { GameAction, GameResponse, GameStatus } from '../../models/types';
import { concat, delay, finalize, map, Observable, of, switchMap, takeUntil, tap } from 'rxjs';
import { UserService } from '../../services/user.service';
import { ToastService } from '../../services/toast.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { extractErrorMessage } from '../../util/errors';

const SUIT_SYMBOLS: Record<string, string> = {
  HEARTS: '♥',
  DIAMONDS: '♦',
  CLUBS: '♣',
  SPADES: '♠',
};

const RANK_SYMBOLS: Record<string, string> = {
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

@Component({
  selector: 'app-game',
  imports: [],
  templateUrl: './game.component.html',
  styleUrl: './game.component.css',
})
export class GameComponent {
  private readonly gameService = inject(GameService);
  private readonly userService = inject(UserService);
  private readonly toast = inject(ToastService);
  private readonly destroyRef = inject(DestroyRef);

  readonly gameState = signal<GameResponse | null>(null);
  readonly betAmount = signal(100);
  readonly isLoading = signal(false);
  readonly isRevealing = signal(false);
  readonly loadingMessage = signal('');

  readonly betValidationMessage = computed<string | null>(() => {
    const amount = this.betAmount();
    if (!Number.isInteger(amount) || amount <= 0) {
      return 'Bet must be a positive whole number';
    }

    const balance = this.userService.currentUser()?.balance;
    if (balance !== undefined && amount > balance) {
      return `You only have ${balance} coins`;
    }

    return null;
  });

  updateBet(event: Event) {
    const input = event.target as HTMLInputElement;
    this.betAmount.set(Number(input.value));
  }

  startGame() {
    if (this.isLoading() || this.isRevealing()) {
      return;
    }
    if (this.betValidationMessage() !== null) {
      return;
    }

    this.isLoading.set(true);
    this.loadingMessage.set('Shuffling deck...');

    this.gameService
      .startGame(this.betAmount())
      .pipe(delay(600), takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (game) => {
          this.gameState.set(game);
          this.isLoading.set(false);
          this.userService.refreshUser();
        },
        error: (err) => {
          this.toast.show(extractErrorMessage(err, 'Could not start the game.'));
          this.isLoading.set(false);
        },
      });
  }

  action(type: GameAction) {
    const game = this.gameState();
    if (!game || game.status !== 'IN_PROGRESS') {
      return;
    }
    if (this.isLoading() || this.isRevealing()) {
      return;
    }

    this.isLoading.set(true);
    this.loadingMessage.set(type === 'STAND' ? 'Dealer is playing...' : 'Dealing card...');

    this.gameService
      .processAction(game.id, type)
      .pipe(
        switchMap((res) => {
          if (res.status === 'IN_PROGRESS') {
            return of(res);
          }

          this.isRevealing.set(true);
          return this.revealDealerHand(res).pipe(finalize(() => this.isRevealing.set(false)));
        }),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe({
        next: (game) => {
          this.gameState.set(game);
          this.isLoading.set(false);
          this.userService.refreshUser();
        },
        error: (err) => {
          this.toast.show(extractErrorMessage(err, 'That move failed.'));
          this.isLoading.set(false);
        },
      });
  }

  private revealDealerHand(finalGame: GameResponse): Observable<GameResponse> {
    const steps: Observable<GameResponse>[] = [
      of(this.partialGame(finalGame, 2)),
      ...finalGame.dealerHand
        .slice(2)
        .map((_, index) => of(this.partialGame(finalGame, 3 + index)).pipe(delay(1000))),
      of(finalGame).pipe(delay(800)),
    ];
    return concat(...steps);
  }

  private partialGame(finalGame: GameResponse, count: number): GameResponse {
    return {
      ...finalGame,
      dealerHand: finalGame.dealerHand.slice(0, count),
      dealerTotal: null,
      dealerComment: null,
    };
  }

  getSuitSymbol(suit: string): string {
    return SUIT_SYMBOLS[suit] ?? '?';
  }

  getRankSymbol(rank: string): string {
    return RANK_SYMBOLS[rank] ?? rank;
  }

  isRed(suit: string): boolean {
    return suit === 'HEARTS' || suit === 'DIAMONDS';
  }

  formatStatus(status: GameStatus): string {
    return status.replaceAll('_', ' ');
  }
}
