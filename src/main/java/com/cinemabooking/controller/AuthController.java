package com.cinemabooking.controller;

import com.cinemabooking.dto.ApiResponse;
import com.cinemabooking.dto.LoginRequest;
import com.cinemabooking.dto.RegisterRequest;
import com.cinemabooking.dto.UserResponse;
import com.cinemabooking.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> register(
            @Valid @RequestBody RegisterRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        UserResponse response = authService.register(request, httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", response));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) {
        UserResponse response = authService.login(request, httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", response));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        authService.logout(httpRequest, httpResponse);
        return ResponseEntity.ok(ApiResponse.ok("Logged out successfully", null));
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        try {
            UserResponse response = authService.getCurrentUserResponse();
            return ResponseEntity.ok(ApiResponse.ok(response));
        } catch (Exception ex) {
            return ResponseEntity.ok(ApiResponse.ok("Anonymous", null));
        }
    }

    @GetMapping("/csrf")
    public ResponseEntity<Map<String, String>> getCsrfToken(CsrfToken token) {
        if (token != null) {
            return ResponseEntity.ok(Map.of(
                    "parameterName", token.getParameterName(),
                    "headerName", token.getHeaderName(),
                    "token", token.getToken()
            ));
        }
        return ResponseEntity.ok(Collections.emptyMap());
    }
}
