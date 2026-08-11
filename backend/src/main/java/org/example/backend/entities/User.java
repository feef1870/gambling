package org.example.backend.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;

@Table(name = "users")
@Entity
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "username", unique = true)
    @NotBlank(message = "Username can not be blank")
    private String username;

    @Column(name = "balance")
    @NotNull(message = "Balance can not be null")
    @Range(min = 0, message = "Balance can not be less than 0")
    private Long balance;

    @Column(name = "created_at")
    @NotNull(message = "(users table) Creation date can not be null")
    private Instant createdAt;

    @Column(name = "last_labor_claim")
    private Instant lastLaborClaim;

    @PrePersist
    public void prePersist() {
        if (this.createdAt == null) {
            this.createdAt = Instant.now();
        }
    }
}
