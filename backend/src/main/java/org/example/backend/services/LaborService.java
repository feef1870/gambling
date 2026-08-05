package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.AppException;
import org.example.backend.repositories.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LaborService {
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public void payWage(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException("User not found", "USER_NOT_FOUND", HttpStatus.NOT_FOUND));

        transactionService.processTransaction(user, 20L, TransactionType.LABOR, null);
    }
}
