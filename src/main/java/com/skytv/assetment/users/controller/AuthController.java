package com.skytv.assetment.users.controller;

import com.skytv.assetment.users.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "JWT authentication API")
public class AuthController {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public record LoginRequest(String email, String password) {}
    public record LoginResponse(String token) {}

    @PostMapping("/login")
    @Operation(summary = "Authenticate and obtain a JWT token")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.email(), req.password())
        );

        var user = userDetailsService.loadUserByUsername(req.email());
        var token = jwtService.generateToken(user);

        return ResponseEntity.ok(new LoginResponse(token));
    }
}
