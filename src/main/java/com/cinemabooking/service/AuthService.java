package com.cinemabooking.service;

import com.cinemabooking.dto.LoginRequest;
import com.cinemabooking.dto.RegisterRequest;
import com.cinemabooking.dto.UserResponse;
import com.cinemabooking.entity.Role;
import com.cinemabooking.entity.User;
import com.cinemabooking.exception.BadRequestException;
import com.cinemabooking.exception.UnauthorizedException;
import com.cinemabooking.repository.UserRepository;
import com.cinemabooking.security.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository = new HttpSessionSecurityContextRepository();

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public UserResponse register(RegisterRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        if (userRepository.existsByEmailIgnoreCase(request.getEmail().trim())) {
            throw new BadRequestException("An account with this email address already exists");
        }

        User user = new User();
        user.setFullName(request.getFullName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setRole(Role.ROLE_CUSTOMER);

        User savedUser = userRepository.save(user);

        // Auto authenticate newly registered user into session
        authenticateAndSetSession(user.getEmail(), request.getPassword(), httpRequest, httpResponse);

        return UserResponse.fromEntity(savedUser);
    }

    public UserResponse login(LoginRequest request, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Authentication authentication = authenticateAndSetSession(request.getEmail().trim(), request.getPassword(), httpRequest, httpResponse);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return UserResponse.fromEntity(userDetails.getUser());
    }

    private Authentication authenticateAndSetSession(String email, String rawPassword, HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        UsernamePasswordAuthenticationToken authReq =
                new UsernamePasswordAuthenticationToken(email, rawPassword);
        Authentication auth = authenticationManager.authenticate(authReq);

        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        sc.setAuthentication(auth);
        SecurityContextHolder.setContext(sc);

        HttpSession session = httpRequest.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, sc);

        if (httpResponse != null) {
            securityContextRepository.saveContext(sc, httpRequest, httpResponse);
        }

        return auth;
    }

    public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        SecurityContext sc = SecurityContextHolder.createEmptyContext();
        SecurityContextHolder.setContext(sc);
        if (httpResponse != null) {
            securityContextRepository.saveContext(sc, httpRequest, httpResponse);
        }
        HttpSession session = httpRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new UnauthorizedException("User is not authenticated");
        }
        if (auth.getPrincipal() instanceof CustomUserDetails userDetails) {
            return userDetails.getUser();
        }
        String email = auth.getName();
        return userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new UnauthorizedException("User session is invalid"));
    }

    public UserResponse getCurrentUserResponse() {
        return UserResponse.fromEntity(getCurrentUser());
    }
}
