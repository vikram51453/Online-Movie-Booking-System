package com.cinemabooking.service;

import com.cinemabooking.dto.MovieRequest;
import com.cinemabooking.dto.MovieResponse;
import com.cinemabooking.entity.Movie;
import com.cinemabooking.exception.ResourceNotFoundException;
import com.cinemabooking.repository.MovieRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final MovieRepository movieRepository;

    public MovieService(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<MovieResponse> getActiveMovies(String search, String genre, String language) {
        String cleanSearch = (search != null && !search.trim().isEmpty()) ? search.trim() : null;
        String cleanGenre = (genre != null && !genre.trim().isEmpty() && !"ALL".equalsIgnoreCase(genre.trim())) ? genre.trim() : null;
        String cleanLanguage = (language != null && !language.trim().isEmpty() && !"ALL".equalsIgnoreCase(language.trim())) ? language.trim() : null;

        List<Movie> movies;
        if (cleanSearch == null && cleanGenre == null && cleanLanguage == null) {
            movies = movieRepository.findByActiveTrueOrderByIdDesc();
        } else {
            movies = movieRepository.searchActiveMovies(cleanSearch, cleanGenre, cleanLanguage);
        }

        return movies.stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<MovieResponse> getAllMoviesAdmin() {
        return movieRepository.findAllByOrderByIdDesc().stream()
                .map(MovieResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public MovieResponse getMovieById(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));
        return MovieResponse.fromEntity(movie);
    }

    public Movie getMovieEntity(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with ID: " + id));
    }

    @Transactional
    public MovieResponse createMovie(MovieRequest request) {
        Movie movie = new Movie();
        copyProperties(request, movie);
        Movie saved = movieRepository.save(movie);
        return MovieResponse.fromEntity(saved);
    }

    @Transactional
    public MovieResponse updateMovie(Long id, MovieRequest request) {
        Movie movie = getMovieEntity(id);
        copyProperties(request, movie);
        Movie saved = movieRepository.save(movie);
        return MovieResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteMovie(Long id) {
        Movie movie = getMovieEntity(id);
        // Soft delete to protect relational integrity with previous bookings
        movie.setActive(false);
        movieRepository.save(movie);
    }

    public List<String> getDistinctGenres() {
        return movieRepository.findDistinctGenres();
    }

    public List<String> getDistinctLanguages() {
        return movieRepository.findDistinctLanguages();
    }

    private void copyProperties(MovieRequest source, Movie target) {
        target.setTitle(source.getTitle().trim());
        target.setDescription(source.getDescription());
        target.setGenre(source.getGenre().trim());
        target.setLanguage(source.getLanguage().trim());
        target.setDurationMinutes(source.getDurationMinutes());
        target.setReleaseDate(source.getReleaseDate());
        target.setPosterUrl(source.getPosterUrl());
        target.setTrailerUrl(source.getTrailerUrl());
        target.setRating(source.getRating());
        target.setActive(source.isActive());
    }
}
