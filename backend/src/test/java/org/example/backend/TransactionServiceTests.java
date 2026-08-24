package org.example.backend;

import org.example.backend.entities.Game;
import org.example.backend.entities.Transaction;
import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.InsufficientFundsException;
import org.example.backend.exception.InvalidAmountException;
import org.example.backend.repositories.TransactionRepository;
import org.example.backend.repositories.UserRepository;
import org.example.backend.services.TransactionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTests {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User userWithBalance(Long balance) {
        User user = new User();
        user.setId("abc");
        user.setBalance(balance);
        return user;
    }

    private void stubRepositoriesToReturnInput(User user) {
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void wagerDecreasesBalanceAndCreatesCorrectTransaction() {
        User user = userWithBalance(100L);
        stubRepositoriesToReturnInput(user);

        transactionService.processTransaction(user, 30L, TransactionType.WAGER, null);

        assertEquals(70L, user.getBalance());
        verify(userRepository).save(user);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(30L, savedTransaction.getAmount());
        assertEquals(user, savedTransaction.getUser());
        assertEquals(TransactionType.WAGER, savedTransaction.getType());
    }

    @Test
    void wagerEqualToBalanceSucceeds() {
        User user = userWithBalance(100L);
        stubRepositoriesToReturnInput(user);

        transactionService.processTransaction(user, 100L, TransactionType.WAGER, null);

        assertEquals(0L, user.getBalance());
        verify(userRepository).save(user);
    }

    @Test
    void wagerLargerThanBalanceFails() {
        User user = userWithBalance(100L);

        assertThrows(InsufficientFundsException.class, () ->
                transactionService.processTransaction(user, 200L, TransactionType.WAGER, null));

        assertEquals(100L, user.getBalance());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @ParameterizedTest
    @EnumSource(value = TransactionType.class,
            names = {"PAYOUT", "REFUND", "LABOR", "SIGNUP_BONUS", "ADMINS_BLESSING"})
    void creditTypesIncreaseBalance(TransactionType type) {
        User user = userWithBalance(100L);
        stubRepositoriesToReturnInput(user);

        transactionService.processTransaction(user, 30L, type, null);

        assertEquals(130L, user.getBalance());
        verify(userRepository).save(user);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(30L, savedTransaction.getAmount());
        assertEquals(user, savedTransaction.getUser());
        assertEquals(type, savedTransaction.getType());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = {0L, -100L})
    void invalidAmountsAreRejected(Long amount) {
        User user = userWithBalance(100L);

        assertThrows(InvalidAmountException.class, () ->
                transactionService.processTransaction(user, amount, TransactionType.WAGER, null));

        assertEquals(100L, user.getBalance());
        verify(userRepository, never()).save(any(User.class));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void transactionIsAssociatedWithGame() {
        User user = userWithBalance(100L);
        stubRepositoriesToReturnInput(user);

        Game game = new Game();
        game.setId(42L);

        transactionService.processTransaction(user, 30L, TransactionType.WAGER, game);

        ArgumentCaptor<Transaction> transactionCaptor = ArgumentCaptor.forClass(Transaction.class);
        verify(transactionRepository).save(transactionCaptor.capture());

        Transaction savedTransaction = transactionCaptor.getValue();

        assertEquals(game, savedTransaction.getGame());
        assertEquals(30L, savedTransaction.getAmount());
        assertEquals(user, savedTransaction.getUser());
        assertEquals(TransactionType.WAGER, savedTransaction.getType());
    }
}
