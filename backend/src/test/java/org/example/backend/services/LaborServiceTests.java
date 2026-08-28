package org.example.backend.services;

import org.example.backend.entities.Transaction;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.LaborCooldownException;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LaborServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private LaborService laborService;

    private User createUserWithLastLaborClaim(Instant lastLaborClaim) {
        User user = new User();
        user.setId("abc");
        user.setLastLaborClaim(lastLaborClaim);

        return user;
    }

    @Test
    void firstClaimSucceeds() {
        User user = createUserWithLastLaborClaim(null);

        when(userRepository.findByIdWithLock(any(String.class))).thenReturn(Optional.of(user));

        laborService.payWage(user.getId());

        verify(userRepository).save(user);
        verify(transactionService).processTransaction(user, 20L, TransactionType.LABOR, null);
        assertNotNull(user.getLastLaborClaim());
    }

    @Test
    void claimDuringCooldownFails() {
        User user = createUserWithLastLaborClaim(Instant.now());

        when(userRepository.findByIdWithLock(any(String.class))).thenReturn(Optional.of(user));

        assertThrows(LaborCooldownException.class, () -> laborService.payWage(user.getId()));

        verify(userRepository, never()).save(any(User.class));
        verify(transactionService, never()).processTransaction(any(), any(), any(), any());
    }

    @Test
    void claimAfterCooldownSucceeds() {
        User user = createUserWithLastLaborClaim(Instant.now().minusSeconds(3));

        when(userRepository.findByIdWithLock(any(String.class))).thenReturn(Optional.of(user));

        laborService.payWage(user.getId());

        verify(userRepository).save(user);
        verify(transactionService).processTransaction(user, 20L, TransactionType.LABOR, null);
    }

    @Test
    void missingUserThrows() {
        when(userRepository.findByIdWithLock(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> laborService.payWage("abc"));

        verify(userRepository, never()).save(any(User.class));
        verify(transactionService, never()).processTransaction(any(), any(), any(), any());
    }

}
