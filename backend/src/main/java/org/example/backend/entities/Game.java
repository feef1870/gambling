package org.example.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.GameStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.Instant;

@Entity
@Table(name = "games")
@Getter
@Setter
@NoArgsConstructor
public class Game {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull(message = "(games table) user_id foreign key can not be null")
    private User user;

    @Column(name = "bet_amount")
    @NotNull(message = "Bet amount can not be null")
    @Positive(message = "Bet amount must be greater than zero")
    private Long betAmount;

    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status")
    @NotNull(message = "Game status can not be null")
    private GameStatus status;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "state")
    @NotNull(message = "Game state can not be null")
    private GameState state;

    @Column(name = "created_at")
    @NotNull(message = "(games table) Creation date can not be null")
    private Instant createdAt;

    @Column(name = "updated_at")
    @NotNull(message = "(games table) Update date can not be null")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
        if (this.updatedAt == null) {
            this.updatedAt = this.createdAt;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}
