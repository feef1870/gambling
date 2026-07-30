package org.example.backend.util;

import org.example.backend.entities.Card;
import org.example.backend.enums.Rank;
import org.example.backend.enums.Suit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckUtil {
    private DeckUtil() {}

    public static List<Card> generateShuffledDeck() {
        List<Card> deck = new ArrayList<>(52);

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                deck.add(new Card(rank, suit));
            }
        }

        Collections.shuffle(deck);
        return deck;
    }
}
