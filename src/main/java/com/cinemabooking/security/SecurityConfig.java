package com.cinemabooking.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        CsrfTokenRequestAttributeHandler requestHandler = new CsrfTokenRequestAttributeHandler();
        requestHandler.setCsrfRequestAttributeName(null);

        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(requestHandler)
                .ignoringRequestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/logout")
            )
            .securityContext(sc -> sc
                .securityContextRepository(new HttpSessionSecurityContextRepository())
                .requireExplicitSave(false)
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authEx) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"success\":false,\"message\":\"Authentication required. Please log in.\"}");
                })
                .accessDeniedHandler((request, response, accessEx) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("{\"success\":false,\"message\":\"Access denied: Insufficient privileges or invalid request.\"}");
                })
            )
            .authorizeHttpRequests(auth -> auth
                // Static web pages and assets
                .requestMatchers(
                    "/", "/index.html", "/*.html",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico"
                ).permitAll()

                // Public auth APIs
                .requestMatchers(
                    "/api/auth/register",
                    "/api/auth/login",
                    "/api/auth/logout",
                    "/api/auth/csrf",
                    "/api/auth/me"
                ).permitAll()

                // Public read APIs
                .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/theatres/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/shows/**").permitAll()

                // Customer APIs
                .requestMatchers(HttpMethod.POST, "/api/bookings").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/bookings/my").authenticated()
                .requestMatchers(HttpMethod.GET, "/api/bookings/*").authenticated()
                .requestMatchers(HttpMethod.POST, "/api/bookings/*/cancel").authenticated()

                // Admin APIs
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/movies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/movies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/movies/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/theatres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/theatres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/theatres/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/screens/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/screens/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/screens/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/shows/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/shows/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/shows/**").hasRole("ADMIN")

                // Any other request must be authenticated
                .anyRequest().authenticated()
            );

        return http.build();
    }

    private static class CsrfCookieFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
            if (csrfToken != null) {
                csrfToken.getToken(); // forces cookie write
            }
            filterChain.doFilter(request, response);
        }
    }
}
