package com.selimhorri.app.unit;

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

// Imports corregidos y completos para la arquitectura DTO
import com.selimhorri.app.domain.User;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.dto.response.collection.DtoCollectionResponse;
import com.selimhorri.app.exception.wrapper.UserObjectNotFoundException;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.impl.UserServiceImpl;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    // PRUEBA UNITARIA 1: Validar que al guardar un UserDTO, se retorna un DTO.
    @Test
    void givenUserDTO_whenSaveUser_thenReturnsUserDTO() {
        // Arrange
        UserDto userDtoToSave = UserDto.builder().firstName("Alex").lastName("Lms").build();
        User userEntity = new User();
        userEntity.setFirstName("Alex");
        userEntity.setLastName("Lms");

        when(this.userRepository.save(any(User.class))).thenReturn(userEntity);

        // Act
        UserDto savedUserDto = this.userService.save(userDtoToSave);

        // Assert
        assertNotNull(savedUserDto);
        assertEquals("Alex", savedUserDto.getFirstName());
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    // PRUEBA UNITARIA 2: Validar que se puede encontrar un usuario por su ID y devuelve un DTO.
    @Test
    void givenExistingUserId_whenFindById_thenReturnsUserDTO() {
        // Arrange
        User userEntity = new User();
        userEntity.setUserId(1);
        when(this.userRepository.findById(1)).thenReturn(Optional.of(userEntity));

        // Act
        UserDto foundUserDto = this.userService.findById(1);

        // Assert
        assertNotNull(foundUserDto);
        assertEquals(1, foundUserDto.getUserId());
    }

    // PRUEBA UNITARIA 3: Validar que se lanza UserNotFoundException si el usuario no existe.
    @Test
    void givenNonExistingUserId_whenFindById_thenThrowsUserNotFoundException() {
        // Arrange
        when(this.userRepository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UserObjectNotFoundException.class, () -> {
            this.userService.findById(99);
        });
    }

    // PRUEBA UNITARIA 4: Validar que el método de actualizar funciona con DTOs.
    @Test
    void givenUserDTO_whenUpdateUser_thenRepositorySaveIsCalled() {
        // Arrange
        UserDto userDtoToUpdate = UserDto.builder().userId(1).firstName("AlexUpdated").build();
        User userEntity = new User();
        userEntity.setUserId(1);
        userEntity.setFirstName("AlexUpdated");

        when(this.userRepository.save(any(User.class))).thenReturn(userEntity);

        // Act
        UserDto updatedUser = this.userService.update(userDtoToUpdate);

        // Assert
        assertNotNull(updatedUser);
        assertEquals("AlexUpdated", updatedUser.getFirstName());
        verify(this.userRepository, times(1)).save(any(User.class));
    }

    // PRUEBA UNITARIA 5: Validar que el método de borrar llama al repositorio.
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
