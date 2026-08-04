package org.example.backend.dto;

import org.example.backend.entities.Card;
import org.example.backend.enums.GameStatus;

import java.util.List;

public record GameResponse(
        Long id,
        Long betAmount,
        GameStatus status,
        List<Card> playerHand,
        int playerTotal,
        List<Card> dealerHand,
        Integer dealerTotal,
        String dealerComment
) {
}
