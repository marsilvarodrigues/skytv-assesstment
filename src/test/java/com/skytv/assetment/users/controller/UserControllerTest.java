package com.skytv.assetment.users.controller;

import com.skytv.assetment.users.dto.UserRequest;
import com.skytv.assetment.users.dto.UserResponse;
import com.skytv.assetment.users.exception.UserDuplicatedException;
import com.skytv.assetment.users.security.JwtService;
import com.skytv.assetment.users.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @MockitoBean
    UserService userService;

    @MockitoBean
    JwtService jwtService;

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserRequest request = new UserRequest("john@example.com", "secret", "John");
        UserResponse response = new UserResponse(1L, "john@example.com", "John", List.of());

        Mockito.when(userService.createUser(any(UserRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void createUser_shouldNotCreated() throws Exception {
        UserRequest request = new UserRequest("john@example.com", "secret", "John");
        UserResponse response = new UserResponse(1L, "john@example.com", "John", List.of());

        Mockito.when(userService.createUser(any(UserRequest.class))).thenThrow(new UserDuplicatedException("User with email already exists"));

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserResponse response = new UserResponse(1L, "john@example.com", "John", List.of());
        Mockito.when(userService.getUser(1L)).thenReturn(response);

        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value("john@example.com"));
    }

    @Test
    void deleteUser_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/users/1"))
                .andExpect(status().isNoContent());
    }
}
