package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.LaborCooldownException;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LaborService {

    private static final Duration COOLDOWN = Duration.ofSeconds(2);
    private static final long WAGE_AMOUNT = 20L;

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public void payWage(String userId) {
        User user = userRepository.findByIdWithLock(userId)
                .orElseThrow(UserNotFoundException::new);

        Instant now = Instant.now();
        if (user.getLastLaborClaim() != null) {
            Instant nextAllowed = user.getLastLaborClaim().plus(COOLDOWN);
            if (now.isBefore(nextAllowed)) {
                long secondsLeft = Duration.between(now, nextAllowed).getSeconds();
                throw new LaborCooldownException(secondsLeft);
            }
        }

        user.setLastLaborClaim(now);
        userRepository.save(user);

        transactionService.processTransaction(user, WAGE_AMOUNT, TransactionType.LABOR, null);
    }
}
