package com.selimhorri.app.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import com.selimhorri.app.dto.CredentialDto;
import com.selimhorri.app.dto.UserDto;
import com.selimhorri.app.repository.CredentialRepository;
import com.selimhorri.app.repository.UserRepository;
import com.selimhorri.app.service.UserService;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD) // Reinicia la BD antes de cada test
class UserIntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CredentialRepository credentialRepository;

    // PRUEBA DE INTEGRACIÓN 1: Guardar un usuario y verificar que se persiste en la BD.
    @Test
    void givenUserDto_whenSave_thenUserIsPersisted() {
        // Arrange
        UserDto userToSave = UserDto.builder()
                .firstName("Integration")
                .lastName("Test")
                .email("integration@test.com")
                .phone("123456")
                .credentialDto(CredentialDto.builder().username("integ-test").password("password").build())
                .build();

        // Act
        UserDto savedUser = this.userService.save(userToSave);

        // Assert
        assertNotNull(savedUser.getUserId(), "El ID del usuario no debería ser nulo después de guardar");
        assertTrue(this.userRepository.findById(savedUser.getUserId()).isPresent(), "El usuario debería existir en la BD");
        assertNotNull(savedUser.getCredentialDto().getCredentialId(), "El ID de la credencial no debería ser nulo");
    }

    // PRUEBA DE INTEGRACIÓN 2: Encontrar un usuario que ya ha sido guardado.
    @Test
    void givenUserId_whenFindById_thenReturnsCorrectUser() {
        // Arrange
        UserDto userToSave = UserDto.builder()
                .firstName("FindMe")
                .credentialDto(CredentialDto.builder().username("find-me").build())
                .build();
        UserDto savedUser = this.userService.save(userToSave);

        // Act
        UserDto foundUser = this.userService.findById(savedUser.getUserId());

        // Assert
        assertNotNull(foundUser);
        assertEquals("FindMe", foundUser.getFirstName());
    }

    // PRUEBA DE INTEGRACIÓN 3: Actualizar un usuario y verificar el cambio en la BD.
    @Test
    void givenUpdatedDto_whenUpdate_thenDataIsUpdatedInDb() {
        // Arrange
        UserDto userToSave = UserDto.builder()
                .firstName("Original")
                .credentialDto(CredentialDto.builder().username("original").build())
                .build();
        UserDto savedUser = this.userService.save(userToSave);

        UserDto toUpdate = this.userService.findById(savedUser.getUserId());
        toUpdate.setFirstName("UpdatedName");

        // Act
        this.userService.update(toUpdate);

        // Assert
        UserDto userFromDb = this.userService.findById(savedUser.getUserId());
        assertEquals("UpdatedName", userFromDb.getFirstName());
    }

    // PRUEBA DE INTEGRACIÓN 4: Borrar un usuario y verificar que ya no está en la BD.
    @Test
    void givenUserId_whenDelete_thenUserIsRemovedFromDb() {
        // Arrange
        UserDto userToSave = UserDto.builder()
                .firstName("ToDelete")
                .credentialDto(CredentialDto.builder().username("to-delete").build())
                .build();
        UserDto savedUser = this.userService.save(userToSave);
        assertTrue(this.userRepository.findById(savedUser.getUserId()).isPresent(), "El usuario debe existir antes de borrarlo");

        // Act
        this.userService.deleteById(savedUser.getUserId());

        // Assert
        assertFalse(this.userRepository.findById(savedUser.getUserId()).isPresent(), "El usuario no debería existir después de borrarlo");
    }

    // PRUEBA DE INTEGRACIÓN 5: Contar todos los usuarios.
    @Test
    void whenFindAll_thenReturnsAllUsers() {
        // Arrange
        this.userService.save(UserDto.builder().firstName("UserA").credentialDto(CredentialDto.builder().username("user-a").build()).build());
        this.userService.save(UserDto.builder().firstName("UserB").credentialDto(CredentialDto.builder().username("user-b").build()).build());

        // Act
        long countAfter = this.userService.findAll().size();
        
        assertEquals(2, countAfter);
    }
}
