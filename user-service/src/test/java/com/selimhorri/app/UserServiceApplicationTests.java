package com.selimhorri.app.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.selimhorri.app.exception.wrapper.UserNotFoundException;
import com.selimhorri.app.model.entity.User;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceApplicationTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void givenUserObject_whenSaveUser_thenReturnsSavedUser() {
        // Arrange
        User userToSave = new User();
        userToSave.setFirstName("Alex");
        userToSave.setLastName("Lms");
        when(this.userRepository.save(any(User.class))).thenReturn(userToSave);

        // Act
        User savedUser = this.userService.save(userToSave);

        // Assert
        assertNotNull(savedUser);
        assertEquals("Alex", savedUser.getFirstName());
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenExistingUserId_whenFindById_thenReturnsUser() {
        // Arrange
        User user = new User();
        user.setUserId(1);
        when(this.userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        User foundUser = this.userService.findById(1);

        // Assert
        assertNotNull(foundUser);
        assertEquals(1, foundUser.getUserId());
    }

    @Test
    void givenNonExistingUserId_whenFindById_thenThrowsUserNotFoundException() {
        // Arrange
        when(this.userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            this.userService.findById(99);
        });
    }

    @Test
    void givenUserObject_whenUpdateUser_thenRepositorySaveIsCalled() {
        // Arrange
        User userToUpdate = new User();
        userToUpdate.setUserId(1);
        userToUpdate.setFirstName("AlexUpdated");
        when(this.userRepository.save(any(User.class))).thenReturn(userToUpdate);

        // Act
        User updatedUser = this.userService.update(userToUpdate);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("AlexUpdated", updatedUser.getFirstName());
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    @Test
    void givenUserId_whenDeleteById_thenRepositoryDeleteIsCalled() {
        // Arrange
        int userId = 1;
        doNothing().when(this.userRepository).deleteById(userId);

        // Act
        this.userService.deleteById(userId);

        // Assert
        verify(this.userRepository, times(1)).deleteById(userId);
    }
}






