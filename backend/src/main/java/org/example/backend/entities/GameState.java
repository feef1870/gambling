package org.example.backend.entities;

import java.util.List;

public record GameState(
        List<Card> deck,
        List<Card> playerHand,
        List<Card> dealerHand,
        int playerTotal,
        int dealerTotal,
        String phase
) {
}
