package com.cinemabooking.controller;

import com.cinemabooking.dto.ApiResponse;
import com.cinemabooking.dto.MovieRequest;
import com.cinemabooking.dto.MovieResponse;
import com.cinemabooking.service.MovieService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
public class MovieController {

    private final MovieService movieService;

    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getMovies(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String language) {
        List<MovieResponse> movies = movieService.getActiveMovies(search, genre, language);
        return ResponseEntity.ok(ApiResponse.ok(movies));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MovieResponse>> getMovieById(@PathVariable Long id) {
        MovieResponse movie = movieService.getMovieById(id);
        return ResponseEntity.ok(ApiResponse.ok(movie));
    }

    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<List<String>>> getGenres() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getDistinctGenres()));
    }

    @GetMapping("/languages")
    public ResponseEntity<ApiResponse<List<String>>> getLanguages() {
        return ResponseEntity.ok(ApiResponse.ok(movieService.getDistinctLanguages()));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> createMovie(@Valid @RequestBody MovieRequest request) {
        MovieResponse movie = movieService.createMovie(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Movie created successfully", movie));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<MovieResponse>> updateMovie(
            @PathVariable Long id,
            @Valid @RequestBody MovieRequest request) {
        MovieResponse movie = movieService.updateMovie(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Movie updated successfully", movie));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.ok(ApiResponse.ok("Movie deactivated successfully", null));
    }
}
