package org.example.backend.services;

import org.example.backend.entities.User;
import org.example.backend.enums.TransactionType;
import org.example.backend.exception.UserNotFoundException;
import org.example.backend.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private UserService userService;


    @Test
    void getCurrentUserExisting() {
        User user = new User();
        user.setId("abc");

        when(userRepository.findById("abc")).thenReturn(Optional.of(user));

        User returnedUser = userService.getCurrentUser("abc");

        assertEquals(user, returnedUser);
    }

    @Test
    void getCurrentUserNonExisting() {
        when(userRepository.findById("abc")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getCurrentUser("abc"));
    }

    @Test
    void syncUserFromJwtExistingUserReturnsIt() {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("abc").claim("preferred_username", "Bob").build();

        User user = new User();
        user.setId("abc");
        user.setUsername("Bob");

        when(userRepository.findById("abc")).thenReturn(Optional.of(user));

        User returnedUser = userService.syncUserFromJwt(jwt);

        assertEquals(user, returnedUser);
        verify(userRepository, never()).save(any());
        verify(transactionService, never()).processTransaction(any(), any(), any(), any());
    }

    @Test
    void syncUserFromJwtCreatesUserWithSignupBonus() {
        Jwt jwt = Jwt.withTokenValue("t").header("alg", "none").subject("abc").claim("preferred_username", "Bob").build();
        when(userRepository.findById("abc")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        userService.syncUserFromJwt(jwt);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);

        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();

        assertEquals("abc", savedUser.getId());
        assertEquals("Bob", savedUser.getUsername());
        assertEquals(0L, savedUser.getBalance());
        assertEquals(savedUser, userService.syncUserFromJwt(jwt));
        verify(transactionService).processTransaction(savedUser, 1000L, TransactionType.SIGNUP_BONUS, null);
    }
}
