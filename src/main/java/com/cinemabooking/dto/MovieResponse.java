package com.cinemabooking.dto;

import com.cinemabooking.entity.Movie;
import java.time.LocalDate;

public class MovieResponse {
    private Long id;
    private String title;
    private String description;
    private String genre;
    private String language;
    private Integer durationMinutes;
    private LocalDate releaseDate;
    private String posterUrl;
    private String trailerUrl;
    private Double rating;
    private boolean active;

    public MovieResponse() {}

    public static MovieResponse fromEntity(Movie movie) {
        if (movie == null) return null;
        MovieResponse dto = new MovieResponse();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setDescription(movie.getDescription());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setDurationMinutes(movie.getDurationMinutes());
        dto.setReleaseDate(movie.getReleaseDate());
        dto.setPosterUrl(movie.getPosterUrl());
        dto.setTrailerUrl(movie.getTrailerUrl());
        dto.setRating(movie.getRating());
        dto.setActive(movie.isActive());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
