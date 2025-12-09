package com.skytv.assetment.users.service;

import com.skytv.assetment.users.dto.ExternalProjectRequest;
import com.skytv.assetment.users.dto.ShortUserResponse;
import com.skytv.assetment.users.dto.UserRequest;
import com.skytv.assetment.users.dto.UserResponse;
import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import com.skytv.assetment.users.exception.ResourceNotFoundException;
import com.skytv.assetment.users.exception.UserDuplicatedException;
import com.skytv.assetment.users.repository.ExternalProjectRepository;
import com.skytv.assetment.users.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    ExternalProjectRepository externalProjectRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserService userService;

    @Test
    void createUser_shouldPersistAndReturnResponse() {
        UserRequest request = new UserRequest("john@example.com", "secret", "John");
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(1L);
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertEquals(1L, response.id());
        assertEquals("john@example.com", response.email());
        assertEquals("John", response.name());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_shouldThrowWhenEmailExists() {
        UserRequest request = new UserRequest("john@example.com", "secret", "John");
        when(passwordEncoder.encode("secret")).thenReturn("encoded-secret");
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        assertThrows(UserDuplicatedException.class, () -> userService.createUser(request));

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUser_shouldReturnUserWhenExists() {
        User user = User.builder()
                .id(1L)
                .email("john@example.com")
                .name("John")
                .password("encoded")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserResponse response = userService.getUser(1L);

        assertEquals(1L, response.id());
        assertEquals("john@example.com", response.email());
        assertEquals("John", response.name());
    }

    @Test
    void getUser_shouldThrowWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUser(99L));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        User existing = User.builder()
                .id(1L)
                .email("old@example.com")
                .name("Old Name")
                .password("old-pass")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(passwordEncoder.encode("new-pass")).thenReturn("encoded-new");
        UserRequest req = new UserRequest("new@example.com", "new-pass", "New Name");

        UserResponse response = userService.updateUser(1L, req);

        assertEquals("new@example.com", response.email());
        assertEquals("New Name", response.name());
        assertEquals(1L, response.id());
        assertEquals("encoded-new", existing.getPassword());
    }

    @Test
    void deleteUser_shouldDeleteWhenExists() {
        User existing = User.builder()
                .id(1L)
                .email("john@example.com")
                .password("encoded")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));

        userService.deleteUser(1L);

        verify(userRepository).delete(existing);
    }

    @Test
    void getAllUsers_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(List.of(
                User.builder().id(1L).email("email@email.com").build()));

        List<ShortUserResponse> users = userService.getAllUsers();
        assertThat(users).hasSize(1);
    }

    @Test
    void addUserToExternalProject_existedProject() {
        var externalProject = new ExternalProjectRequest(UUID.randomUUID().toString(), "test");
        var externalProjectEntity = mock(ExternalProject.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(externalProjectRepository.findById(anyString())).thenReturn(Optional.of(externalProjectEntity));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(externalProjectEntity);

        userService.addExternalProject(1L, externalProject);

        verify(externalProjectRepository).save(externalProjectEntity);
    }

    @Test
    void addUserToExternalProject_newProject() {
        var externalProject = new ExternalProjectRequest("", "test");
        var externalProjectEntity = mock(ExternalProject.class);

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(User.builder().id(1L).build()));
        when(externalProjectRepository.save(any(ExternalProject.class))).thenReturn(externalProjectEntity);

        userService.addExternalProject(1L, externalProject);

        verify(externalProjectRepository, never()).findById(anyString());
        verify(externalProjectRepository).save(any(ExternalProject.class));
    }

}
