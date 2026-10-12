package com.cinemabooking;

import com.cinemabooking.dto.MovieRequest;
import com.cinemabooking.entity.Movie;
import com.cinemabooking.entity.Role;
import com.cinemabooking.entity.User;
import com.cinemabooking.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MovieControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private ShowRepository showRepository;

    @Autowired
    private BookingSeatRepository bookingSeatRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setUp() {
        bookingSeatRepository.deleteAll();
        bookingRepository.deleteAll();
        showRepository.deleteAll();
        movieRepository.deleteAll();
    }

    @Test
    @DisplayName("Public user can browse active movies")
    void testGetMoviesPublic() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Interstellar");
        movie.setGenre("Sci-Fi");
        movie.setLanguage("English");
        movie.setDurationMinutes(169);
        movie.setRating(8.7);
        movie.setActive(true);
        movieRepository.save(movie);

        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.data[0].title", is("Interstellar")));
    }

    @Test
    @DisplayName("Admin can create new movie")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testCreateMovieAdminSuccess() throws Exception {
        MovieRequest req = new MovieRequest();
        req.setTitle("Gladiator II");
        req.setGenre("Action / Drama");
        req.setLanguage("English");
        req.setDurationMinutes(150);
        req.setRating(8.5);
        req.setReleaseDate(LocalDate.of(2024, 11, 22));
        req.setActive(true);

        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data.title", is("Gladiator II")));
    }

    @Test
    @DisplayName("Customer cannot create movie (Forbidden 403)")
    @WithMockUser(username = "customer@example.com", roles = {"CUSTOMER"})
    void testCreateMovieForbiddenForCustomer() throws Exception {
        MovieRequest req = new MovieRequest();
        req.setTitle("Unauthorized Movie");
        req.setGenre("Action");
        req.setLanguage("English");
        req.setDurationMinutes(120);

        mockMvc.perform(post("/api/movies")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Admin can update and soft-delete movie")
    @WithMockUser(username = "admin@example.com", roles = {"ADMIN"})
    void testUpdateAndDeleteMovieAdmin() throws Exception {
        Movie movie = new Movie();
        movie.setTitle("Original Title");
        movie.setGenre("Action");
        movie.setLanguage("English");
        movie.setDurationMinutes(120);
        movie.setActive(true);
        Movie saved = movieRepository.save(movie);

        MovieRequest updateReq = new MovieRequest();
        updateReq.setTitle("Updated Title");
        updateReq.setGenre("Action / Sci-Fi");
        updateReq.setLanguage("English");
        updateReq.setDurationMinutes(130);
        updateReq.setActive(true);

        mockMvc.perform(put("/api/movies/" + saved.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title", is("Updated Title")));

        mockMvc.perform(delete("/api/movies/" + saved.getId()).with(csrf()))
                .andExpect(status().isOk());

        // Once deactivated, public listing should not return it
        mockMvc.perform(get("/api/movies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)));
    }
}
