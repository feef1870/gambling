package org.example.backend.services;

import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.Game;
import org.example.backend.entities.Transaction;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.InsufficientFundsException;
import org.example.backend.repositories.TransactionRepository;
import org.example.backend.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    public Transaction processTransaction(User user, Long amount, TransactionType type, @Nullable Game game) {
        long newBalance = switch (type) {
            case WAGER -> {
                if (user.getBalance() < amount) {
                    throw new InsufficientFundsException("User balance is lower than wager amount");
                }
                yield user.getBalance() - amount;
            }
            case PAYOUT, REFUND, SIGNUP_BONUS, LABOR, ADMINS_BLESSING -> user.getBalance() + amount;
            default -> throw new IllegalArgumentException("Unknown transaction type: " + type);
        };

        user.setBalance(newBalance);
        User savedUser = userRepository.save(user);

        Transaction transaction = new Transaction();
        transaction.setUser(savedUser);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setGame(game);

        return transactionRepository.save(transaction);
    }
}
