package org.example.backend.services;

import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.UserCreationConflictException;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TransactionService transactionService;

    @Transactional
    public User syncUserFromJwt(Jwt jwt) {
        String keycloakId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");

        return userRepository.findById(keycloakId).orElseGet(() -> {
            try {
                return createNewUser(keycloakId, username);
            } catch (DataIntegrityViolationException e) {
                return userRepository.findById(keycloakId)
                        .orElseThrow(UserCreationConflictException::new);
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    protected User createNewUser(String keycloakId, String username) {
        User newUser = new User();
        newUser.setId(keycloakId);
        newUser.setUsername(username);
        newUser.setBalance(0L);
        User savedUser = userRepository.save(newUser);
        transactionService.processTransaction(savedUser, 1000L, TransactionType.SIGNUP_BONUS, null);
        return savedUser;
    }

    public User getCurrentUser(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
    }
}
