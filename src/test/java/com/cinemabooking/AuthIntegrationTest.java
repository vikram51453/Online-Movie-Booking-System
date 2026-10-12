package com.cinemabooking;

import com.cinemabooking.dto.LoginRequest;
import com.cinemabooking.dto.RegisterRequest;
import com.cinemabooking.entity.Role;
import com.cinemabooking.entity.User;
import com.cinemabooking.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("Should successfully register a new customer")
    void testRegisterSuccess() throws Exception {
        RegisterRequest req = new RegisterRequest("Alice Johnson", "alice@example.com", "Password@123", "9876543210");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("alice@example.com")))
                .andExpect(jsonPath("$.data.fullName", is("Alice Johnson")))
                .andExpect(jsonPath("$.data.role", is("ROLE_CUSTOMER")));
    }

    @Test
    @DisplayName("Should fail registration with duplicate email")
    void testRegisterDuplicateEmail() throws Exception {
        User existing = new User("Alice Existing", "alice@example.com", passwordEncoder.encode("Password@123"), "9876543210", Role.ROLE_CUSTOMER);
        userRepository.save(existing);

        RegisterRequest req = new RegisterRequest("Alice Second", "alice@example.com", "NewPassword@123", "9876543210");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should fail registration with invalid input")
    void testRegisterValidationFailure() throws Exception {
        RegisterRequest req = new RegisterRequest("", "invalid-email", "123", "");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should successfully log in existing user")
    void testLoginSuccess() throws Exception {
        User user = new User("Bob Smith", "bob@example.com", passwordEncoder.encode("SecretPass123"), "9876543210", Role.ROLE_CUSTOMER);
        userRepository.save(user);

        LoginRequest loginReq = new LoginRequest("bob@example.com", "SecretPass123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.email", is("bob@example.com")));
    }

    @Test
    @DisplayName("Should fail login with incorrect password")
    void testLoginInvalidCredentials() throws Exception {
        User user = new User("Bob Smith", "bob@example.com", passwordEncoder.encode("SecretPass123"), "9876543210", Role.ROLE_CUSTOMER);
        userRepository.save(user);

        LoginRequest loginReq = new LoginRequest("bob@example.com", "WrongPassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    @DisplayName("Should return CSRF token info")
    void testCsrfEndpoint() throws Exception {
        mockMvc.perform(get("/api/auth/csrf"))
                .andExpect(status().isOk());
    }
}
