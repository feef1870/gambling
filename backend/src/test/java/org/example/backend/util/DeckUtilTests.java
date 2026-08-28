package org.example.backend.util;


import org.example.backend.entities.Card;
import org.example.backend.enums.Rank;
import org.example.backend.enums.Suit;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class DeckUtilTests {

    @Test
    void generatedDeckIsACompleteStandardDeck() {
        List<Card> deck = DeckUtil.generateShuffledDeck();
        assertEquals(52, deck.size());

        Set<Card> distinct = new HashSet<>(deck);
        assertEquals(52, distinct.size());

        Map<Suit, Integer> suitCount = new EnumMap<>(Suit.class);
        for (Card card : deck) {
            suitCount.merge(card.suit(), 1, Integer::sum);
        }
        for (Suit suit : Suit.values()) {
            assertEquals(13, suitCount.get(suit), "wrong count for suit " + suit);
        }

        Map<Rank, Integer> rankCount = new EnumMap<>(Rank.class);
        for (Card card : deck) {
            rankCount.merge(card.rank(), 1, Integer::sum);
        }
        for (Rank rank : Rank.values()) {
            assertEquals(4, rankCount.get(rank), "wrong count for rank " + rank);
        }

    }
}
