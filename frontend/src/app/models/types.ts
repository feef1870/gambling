export type GameStatus = 'IN_PROGRESS' | 'PLAYER_WON' | 'PLAYER_BLACKJACK' | 'DEALER_WON' | 'PUSH' | 'CANCELLED';

export type GameAction = 'HIT' | 'STAND';

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
  dealerComment: string | null;
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
  createdAt: string;
}

export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface ErrorResponse {
  errorCode: string;
  message: string;
  status: number;
  timestamp: string;
  path: string;
}
