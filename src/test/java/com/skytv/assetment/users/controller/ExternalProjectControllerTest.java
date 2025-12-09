package com.skytv.assetment.users.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skytv.assetment.users.dto.ExternalProjectResponse;
import com.skytv.assetment.users.dto.FullExternalProjectResponse;
import com.skytv.assetment.users.dto.ShortUserResponse;
import com.skytv.assetment.users.security.JwtService;
import com.skytv.assetment.users.service.ExternalProjectService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ExternalProjectController.class)
@AutoConfigureMockMvc(addFilters = false)
class ExternalProjectControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ExternalProjectService externalProjectService;

    @MockitoBean
    JwtService jwtService;

    @Test
    void getExternalProject_shouldReturnProject() throws Exception {
        String projectId = UUID.randomUUID().toString();

        FullExternalProjectResponse response = new FullExternalProjectResponse(
                projectId,
                "My External Project",
                List.of(
                        new ShortUserResponse(1L, "user1@example.com", "User One"),
                        new ShortUserResponse(2L, "user2@example.com", "User Two")
                )
        );

        Mockito.when(externalProjectService.getExternalProject(projectId))
                .thenReturn(response);

        mockMvc.perform(get("/api/external-projects/{id}", projectId)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(projectId))
                .andExpect(jsonPath("$.name").value("My External Project"))
                .andExpect(jsonPath("$.users", hasSize(2)))
                .andExpect(jsonPath("$.users[0].id").value(1L));
    }

    @Test
    void getAllExternalProjects_shouldReturnList() throws Exception {
        ExternalProjectResponse p1 = new ExternalProjectResponse(
                UUID.randomUUID().toString(),
                "Project A"
        );
        ExternalProjectResponse p2 = new ExternalProjectResponse(
                UUID.randomUUID().toString(),
                "Project B"
        );

        Mockito.when(externalProjectService.getAllExternalProjects())
                .thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/external-projects/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Project A"))
                .andExpect(jsonPath("$[1].name").value("Project B"));
    }

    @Test
    void getAllExternalProjects_shouldReturnEmptyList() throws Exception {
        Mockito.when(externalProjectService.getAllExternalProjects())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/external-projects/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }
}
