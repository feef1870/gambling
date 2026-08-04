package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.AppException;
import org.example.backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void addBalance(String userId, Long amount) {
        if (amount <= 0) {
            throw new AppException("Amount must be positive", "INVALID_AMOUNT", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        transactionService.processTransaction(user, amount, TransactionType.ADMINS_BLESSING, null);
    }
}
