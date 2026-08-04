package org.example.backend.util;

import org.example.backend.dto.GameResponse;
import org.example.backend.entities.Card;
import org.example.backend.entities.Game;
import org.example.backend.entities.GameState;
import org.example.backend.enums.GameStatus;

import java.util.List;

public class GameMapper {

    private GameMapper() {}

    public static GameResponse toResponse(Game game, String dealerComment) {
        GameState state = game.getState();
        boolean isInProgress = game.getStatus() == GameStatus.IN_PROGRESS;

        List<Card> displayDealerHand = state.dealerHand();
        Integer displayDealerTotal = state.dealerTotal();

        if (isInProgress && !displayDealerHand.isEmpty()) {
            displayDealerHand = List.of(displayDealerHand.getFirst());
            displayDealerTotal = null;
        }

        return new GameResponse(
                game.getId(),
                game.getBetAmount(),
                game.getStatus(),
                state.playerHand(),
                state.playerTotal(),
                displayDealerHand,
                displayDealerTotal,
                dealerComment
        );
    }

    public static GameResponse toResponse(Game game) {
        return toResponse(game, null);
    }
}
