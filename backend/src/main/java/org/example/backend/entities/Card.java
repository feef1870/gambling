package org.example.backend.entities;

import org.example.backend.enums.Rank;
import org.example.backend.enums.Suit;

public record Card(Rank rank, Suit suit) {
}
