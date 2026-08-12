package org.example.backend.entities;

import org.example.backend.enums.GamePhase;

import java.util.List;

public record GameState(
        List<Card> deck,
        List<Card> playerHand,
        List<Card> dealerHand,
        int playerTotal,
        int dealerTotal,
        GamePhase phase
) {
}
