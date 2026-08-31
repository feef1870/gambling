package org.example.backend.services;

import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private AdminService adminService;


    @ParameterizedTest
    @ValueSource(longs = {0L, 100L})
    void addBalanceDelegatesWithBlessingType(Long amount) {
        User user = new User();
        user.setId("abc");

        when(userRepository.findById("abc")).thenReturn(Optional.of(user));

        adminService.addBalance("abc", amount);

        verify(transactionService).processTransaction(user, amount, TransactionType.ADMINS_BLESSING, null);
    }

    @Test
    void addBalanceMissingUserThrows() {
        when(userRepository.findById("abc")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> adminService.addBalance("abc", 100L));

        verify(transactionService, never()).processTransaction(any(), any(), any(), any());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"   "})
    void getUsersWithEmptySearchUsesFindAll(String search) {
        when(userRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

        adminService.getUsers(search, 0, 10);

        verify(userRepository).findAll(any(Pageable.class));
    }

    @Test
    void getUsersWithSearchTrimsInput() {
        adminService.getUsers("   abc     ", 0, 10);

        verify(userRepository).findByUsernameContainingIgnoreCase(eq("abc"), any(Pageable.class));
    }
}
