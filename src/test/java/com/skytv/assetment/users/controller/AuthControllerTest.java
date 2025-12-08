package com.skytv.assetment.users.controller;

import com.skytv.assetment.users.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    AuthenticationManager authManager;

    @MockitoBean
    UserDetailsService userDetailsService;

    @MockitoBean
    JwtService jwtService;

    @Test
    void login_shouldReturnToken() throws Exception {
        UserDetails user = org.springframework.security.core.userdetails.User
                .withUsername("john@example.com")
                .password("encoded")
                .roles("USER")
                .build();

        Mockito.when(userDetailsService.loadUserByUsername("john@example.com")).thenReturn(user);
        Mockito.when(jwtService.generateToken(user)).thenReturn("fake-jwt-token");

        String body = "{\"email\":\"john@example.com\",\"password\":\"secret\"}";

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"));
    }
}
