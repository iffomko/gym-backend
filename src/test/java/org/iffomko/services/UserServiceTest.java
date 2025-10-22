package org.iffomko.services;

import org.iffomko.domain.User;
import org.iffomko.exceptions.LocalizedException;
import org.iffomko.repositories.UserRepository;
import org.iffomko.validators.UserValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserValidator userValidator;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1);
        testUser.setPhone("+79123456789");
        testUser.setPassword("password123");
        testUser.setFirstName("Иван");
        testUser.setLastName("Иванов");
    }

    @Test
    void byIdShouldReturnUserWhenExists() {
        // Given
        int userId = 1;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.byId(userId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getId(), result.get().getId());
        assertEquals(testUser.getPhone(), result.get().getPhone());
        verify(userRepository).findById(userId);
    }

    @Test
    void byIdShouldReturnEmptyWhenNotExists() {
        // Given
        int userId = 999;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.byId(userId);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findById(userId);
    }

    @Test
    void byPhoneShouldReturnUserWhenExists() {
        // Given
        String phone = "+79123456789";
        when(userRepository.findByPhone(phone)).thenReturn(Optional.of(testUser));

        // When
        Optional<User> result = userService.byPhone(phone);

        // Then
        assertTrue(result.isPresent());
        assertEquals(testUser.getPhone(), result.get().getPhone());
        verify(userRepository).findByPhone(phone);
    }

    @Test
    void byPhoneShouldReturnEmptyWhenNotExists() {
        // Given
        String phone = "+79999999999";
        when(userRepository.findByPhone(phone)).thenReturn(Optional.empty());

        // When
        Optional<User> result = userService.byPhone(phone);

        // Then
        assertFalse(result.isPresent());
        verify(userRepository).findByPhone(phone);
    }

    @Test
    void loginShouldReturnUserWhenValidCredentials() {
        // Given
        User loginUser = new User();
        loginUser.setPhone("+79123456789");
        loginUser.setPassword("password123");

        when(userRepository.findByPhone(loginUser.getPhone())).thenReturn(Optional.of(testUser));

        // When
        var actual = userService.login(loginUser);

        assertEquals(testUser.getId(), actual.getId());
        assertEquals(testUser.getFirstName(), actual.getFirstName());
        assertEquals(testUser.getLastName(), actual.getLastName());
        assertEquals(testUser.getPassword(), actual.getPassword());
    }

    @Test
    void loginShouldThrowExceptionWhenUserNotFound() {
        // Given
        User loginUser = new User();
        loginUser.setPhone("+79999999999");
        loginUser.setPassword("password123");

        when(userRepository.findByPhone(loginUser.getPhone())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(LocalizedException.class,
                () -> userService.login(loginUser));

        verify(userRepository).findByPhone(loginUser.getPhone());
    }

    @Test
    void loginShouldThrowExceptionWhenInvalidPassword() {
        // Given
        User loginUser = new User();
        loginUser.setPhone("+79123456789");
        loginUser.setPassword("wrongpassword");

        when(userRepository.findByPhone(loginUser.getPhone())).thenReturn(Optional.of(testUser));

        // When & Then
        assertThrows(LocalizedException.class,
                () -> userService.login(loginUser));

        verify(userRepository).findByPhone(loginUser.getPhone());
    }
}
