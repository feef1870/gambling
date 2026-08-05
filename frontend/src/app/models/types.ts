export type GameStatus = 'IN_PROGRESS' | 'PLAYER_WON' | 'PLAYER_BLACKJACK' | 'DEALER_WON' | 'PUSH' | 'CANCELLED';

export interface Card {
  suit: string;
  rank: string;
}

export interface GameResponse {
  id: number;
  betAmount: number;
  status: GameStatus;
  playerHand: Card[];
  playerTotal: number;
  dealerHand: Card[];
  dealerTotal: number | null;
  dealerComment?: string;
}

export interface UserResponse {
  id: string;
  username: string;
  balance: number;
}

export interface User {
  id: string;
  username: string;
  balance: number;
  createdAt: Date;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}
