package org.example.backend.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.repositories.UserRepository;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

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
            User newUser = new User();
            newUser.setId(keycloakId);
            newUser.setUsername(username);
            newUser.setBalance(0L);

            User savedUser = userRepository.save(newUser);

            transactionService.processTransaction(savedUser, 1000L, TransactionType.SIGNUP_BONUS, null);

            return savedUser;
        });
    }
}
