package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AdminService {
    private final UserRepository userRepository;
    private final TransactionService transactionService;

    public Page<User> getUsers(String search, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        if (search == null || search.trim().isEmpty()) {
            return userRepository.findAll(pageable);
        }

        return userRepository.findByUsernameContainingIgnoreCase(search.trim(), pageable);
    }

    @Transactional
    public void addBalance(String userId, Long amount) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        transactionService.processTransaction(user, amount, TransactionType.ADMINS_BLESSING, null);
    }
}
