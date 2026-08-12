package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.Card;
import org.example.backend.entities.Game;
import org.example.backend.entities.GameState;
import org.example.backend.entities.User;
import org.example.backend.enums.GamePhase;
import org.example.backend.enums.GameStatus;
import org.example.backend.enums.Rank;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.*;
import org.example.backend.repositories.GameRepository;
import org.example.backend.repositories.UserRepository;
import org.example.backend.util.DeckUtil;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;
    private final GameRepository gameRepository;
    private final AiDealerService aiDealerService;

    @Transactional
    public Game startGame(String userId, Long betAmount) {
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(UserNotFoundException::new);

        transactionService.processTransaction(user, betAmount, TransactionType.WAGER, null);

        List<Card> deck = DeckUtil.generateShuffledDeck();
        List<Card> playerHand = new ArrayList<>();
        List<Card> dealerHand = new ArrayList<>();

        playerHand.add(drawCard(deck));
        dealerHand.add(drawCard(deck));
        playerHand.add(drawCard(deck));
        dealerHand.add(drawCard(deck));

        int playerTotal = calculateTotal(playerHand);
        int dealerTotal = calculateTotal(dealerHand);

        Game game = new Game();
        game.setUser(user);
        game.setBetAmount(betAmount);

        if (playerTotal == 21) {
            game.setStatus(GameStatus.PLAYER_BLACKJACK);
        } else {
            game.setStatus(GameStatus.IN_PROGRESS);
        }

        GameState state = new GameState(deck, playerHand, dealerHand, playerTotal, dealerTotal, GamePhase.INITIAL);
        game.setState(state);

        Game savedGame = gameRepository.save(game);

        if (savedGame.getStatus() == GameStatus.PLAYER_BLACKJACK) {
            long payout = betAmount + (long) (betAmount * 1.5);
            transactionService.processTransaction(user, payout, TransactionType.PAYOUT, savedGame);
        }

        return savedGame;
    }

    @Transactional
    public Game processPlayerAction(Long gameId, String userId, String action) {
        Game game = gameRepository.findByIdWithLock(gameId)
                .orElseThrow(GameNotFoundException::new);

        if (!game.getUser().getId().equals(userId)) {
            throw new NotGameOwnerException();
        }
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException();
        }

        GameState state = game.getState();
        List<Card> deck = new ArrayList<>(state.deck());
        List<Card> playerHand = new ArrayList<>(state.playerHand());
        int playerTotal = state.playerTotal();

        if ("HIT".equalsIgnoreCase(action)) {
            playerHand.add(drawCard(deck));
            playerTotal = calculateTotal(playerHand);

            if (playerTotal > 21) {
                game.setStatus(GameStatus.DEALER_WON);
            }

            game.setState(new GameState(deck, playerHand, state.dealerHand(), playerTotal, state.dealerTotal(), GamePhase.PLAYER_TURN));
            return gameRepository.save(game);

        } else if ("STAND".equalsIgnoreCase(action)) {
            return executeDealerTurn(game, deck, playerHand, playerTotal);
        } else {
            throw new InvalidGameActionException();
        }
    }

    private Game executeDealerTurn(Game game, List<Card> deck, List<Card> playerHand, int playerTotal) {
        GameState state = game.getState();
        List<Card> dealerHand = new ArrayList<>(state.dealerHand());
        int dealerTotal = state.dealerTotal();

        while (dealerTotal < 17) {
            dealerHand.add(drawCard(deck));
            dealerTotal = calculateTotal(dealerHand);
        }

        if (dealerTotal > 21 || playerTotal > dealerTotal) {
            game.setStatus(GameStatus.PLAYER_WON);
        } else if (playerTotal == dealerTotal) {
            game.setStatus(GameStatus.PUSH);
        } else {
            game.setStatus(GameStatus.DEALER_WON);
        }

        game.setState(new GameState(deck, playerHand, dealerHand, playerTotal, dealerTotal, GamePhase.FINISHED));
        Game savedGame = gameRepository.save(game);

        if (savedGame.getStatus() == GameStatus.PLAYER_WON) {
            transactionService.processTransaction(savedGame.getUser(), savedGame.getBetAmount() * 2, TransactionType.PAYOUT, savedGame);
        } else if (savedGame.getStatus() == GameStatus.PUSH) {
            transactionService.processTransaction(savedGame.getUser(), savedGame.getBetAmount(), TransactionType.REFUND, savedGame);
        }

        return savedGame;
    }

    private Card drawCard(List<Card> deck) {
        if (deck.isEmpty()) {
            throw new IllegalStateException("Deck is empty");
        }
        return deck.removeLast();
    }

    private int calculateTotal(List<Card> hand) {
        int total = 0;
        int aces = 0;

        for (Card card : hand) {
            total += card.rank().getValue();
            if (card.rank() == Rank.ACE) {
                aces++;
            }
        }

        while (total > 21 && aces > 0) {
            total -= 10;
            aces--;
        }

        return total;
    }

    public String getAiCommentForGame(Game game) {
        if (game.getStatus() == GameStatus.IN_PROGRESS) {
            return null;
        }

        return aiDealerService.getDealerComment(
                game.getStatus().name(),
                game.getState().playerTotal(),
                game.getState().dealerTotal(),
                game.getBetAmount().intValue()
        );
    }
}
