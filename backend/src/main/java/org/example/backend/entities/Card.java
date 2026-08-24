package org.example.backend.entities;

import org.example.backend.enums.Rank;
import org.example.backend.enums.Suit;

import java.util.Objects;

public record Card(Rank rank, Suit suit) {
    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Card card = (Card) o;
        return rank == card.rank && suit == card.suit;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rank, suit);
    }
}
