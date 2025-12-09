package com.skytv.assetment.users.service;

import com.skytv.assetment.users.dto.ExternalProjectResponse;
import com.skytv.assetment.users.dto.FullExternalProjectResponse;
import com.skytv.assetment.users.dto.ShortUserResponse;
import com.skytv.assetment.users.entity.ExternalProject;
import com.skytv.assetment.users.entity.User;
import com.skytv.assetment.users.exception.ResourceNotFoundException;
import com.skytv.assetment.users.repository.ExternalProjectRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalProjectServiceTest {

    @Mock
    ExternalProjectRepository externalProjectRepository;

    @InjectMocks
    ExternalProjectService externalProjectService;

    @Test
    void getExternalProject_shouldReturnFullResponseWhenExists() {
        String projectId = UUID.randomUUID().toString();

        User user1 = User.builder()
                .id(1L)
                .email("user1@example.com")
                .name("User One")
                .build();

        User user2 = User.builder()
                .id(2L)
                .email("user2@example.com")
                .name("User Two")
                .build();

        ExternalProject project = ExternalProject.builder()
                .id(projectId)
                .name("My External Project")
                .users(Set.of(user1, user2))
                .build();

        when(externalProjectRepository.findById(projectId))
                .thenReturn(java.util.Optional.of(project));

        FullExternalProjectResponse response = externalProjectService.getExternalProject(projectId);

        assertEquals(projectId, response.id());
        assertEquals("My External Project", response.name());
        assertThat(response.users()).hasSize(2);

        // check mapping of users
        assertThat(response.users())
                .extracting(ShortUserResponse::id)
                .containsExactlyInAnyOrder(1L, 2L);

        assertThat(response.users())
                .extracting(ShortUserResponse::email)
                .containsExactlyInAnyOrder("user1@example.com", "user2@example.com");
    }

    @Test
    void getExternalProject_shouldThrowWhenNotFound() {
        String projectId = UUID.randomUUID().toString();

        when(externalProjectRepository.findById(projectId))
                .thenReturn(java.util.Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> externalProjectService.getExternalProject(projectId));
    }

    @Test
    void getAllExternalProjects_shouldReturnList() {
        ExternalProject p1 = ExternalProject.builder()
                .id(UUID.randomUUID().toString())
                .name("Project A")
                .build();

        ExternalProject p2 = ExternalProject.builder()
                .id(UUID.randomUUID().toString())
                .name("Project B")
                .build();

        when(externalProjectRepository.findAll())
                .thenReturn(List.of(p1, p2));

        List<ExternalProjectResponse> response = externalProjectService.getAllExternalProjects();

        assertThat(response).hasSize(2);
        assertThat(response)
                .extracting(ExternalProjectResponse::name)
                .containsExactlyInAnyOrder("Project A", "Project B");
    }

    @Test
    void getAllExternalProjects_shouldReturnEmptyListWhenNoneExists() {
        when(externalProjectRepository.findAll())
                .thenReturn(List.of());

        List<ExternalProjectResponse> response = externalProjectService.getAllExternalProjects();

        assertThat(response).isEmpty();
    }
}
