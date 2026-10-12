package com.cinemabooking.dto;

import com.cinemabooking.entity.Show;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class ShowResponse {
    private Long id;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private Integer movieDuration;
    private Long screenId;
    private String screenName;
    private String screenType;
    private Long theatreId;
    private String theatreName;
    private String theatreCity;
    private LocalDate showDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private BigDecimal basePrice;
    private boolean active;

    public ShowResponse() {}

    public static ShowResponse fromEntity(Show show) {
        if (show == null) return null;
        ShowResponse dto = new ShowResponse();
        dto.setId(show.getId());
        if (show.getMovie() != null) {
            dto.setMovieId(show.getMovie().getId());
            dto.setMovieTitle(show.getMovie().getTitle());
            dto.setMoviePosterUrl(show.getMovie().getPosterUrl());
            dto.setMovieDuration(show.getMovie().getDurationMinutes());
        }
        if (show.getScreen() != null) {
            dto.setScreenId(show.getScreen().getId());
            dto.setScreenName(show.getScreen().getName());
            dto.setScreenType(show.getScreen().getScreenType());
            if (show.getScreen().getTheatre() != null) {
                dto.setTheatreId(show.getScreen().getTheatre().getId());
                dto.setTheatreName(show.getScreen().getTheatre().getName());
                dto.setTheatreCity(show.getScreen().getTheatre().getCity());
            }
        }
        dto.setShowDate(show.getShowDate());
        dto.setStartTime(show.getStartTime());
        dto.setEndTime(show.getEndTime());
        dto.setBasePrice(show.getBasePrice());
        dto.setActive(show.isActive());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(Long movieId) {
        this.movieId = movieId;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePosterUrl() {
        return moviePosterUrl;
    }

    public void setMoviePosterUrl(String moviePosterUrl) {
        this.moviePosterUrl = moviePosterUrl;
    }

    public Integer getMovieDuration() {
        return movieDuration;
    }

    public void setMovieDuration(Integer movieDuration) {
        this.movieDuration = movieDuration;
    }

    public Long getScreenId() {
        return screenId;
    }

    public void setScreenId(Long screenId) {
        this.screenId = screenId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public String getScreenType() {
        return screenType;
    }

    public void setScreenType(String screenType) {
        this.screenType = screenType;
    }

    public Long getTheatreId() {
        return theatreId;
    }

    public void setTheatreId(Long theatreId) {
        this.theatreId = theatreId;
    }

    public String getTheatreName() {
        return theatreName;
    }

    public void setTheatreName(String theatreName) {
        this.theatreName = theatreName;
    }

    public String getTheatreCity() {
        return theatreCity;
    }

    public void setTheatreCity(String theatreCity) {
        this.theatreCity = theatreCity;
    }

    public LocalDate getShowDate() {
        return showDate;
    }

    public void setShowDate(LocalDate showDate) {
        this.showDate = showDate;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
