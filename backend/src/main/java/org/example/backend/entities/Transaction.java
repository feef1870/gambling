package org.example.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.enums.TransactionType;

import java.time.Instant;

@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
public class Transaction {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @NotNull(message = "(transactions table) user_id foreign key can not be null")
    private User user;

    @Column(name = "amount")
    @NotNull(message = "Amount can not be null")
    @Positive(message = "Transaction amount must be greater than zero")
    private Long amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    @NotNull(message = "Transaction type can not be null")
    private TransactionType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reference_id")
    private Game game;

    @Column(name = "created_at")
    @NotNull(message = "(transactions table) Creation date can not be null")
    private Instant createdAt;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
