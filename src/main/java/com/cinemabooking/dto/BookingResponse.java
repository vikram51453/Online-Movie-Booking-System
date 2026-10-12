package com.cinemabooking.dto;

import com.cinemabooking.entity.Booking;
import com.cinemabooking.entity.BookingStatus;
import com.cinemabooking.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class BookingResponse {
    private Long id;
    private String bookingReference;
    private Long userId;
    private String userName;
    private String userEmail;
    private Long movieId;
    private String movieTitle;
    private String moviePosterUrl;
    private String theatreName;
    private String theatreCity;
    private String theatreAddress;
    private String screenName;
    private String screenType;
    private LocalDate showDate;
    private LocalTime startTime;
    private List<String> seatNumbers;
    private Integer seatCount;
    private BigDecimal totalAmount;
    private BookingStatus status;
    private PaymentStatus paymentStatus;
    private LocalDateTime bookingTime;

    public BookingResponse() {}

    public static BookingResponse fromEntity(Booking booking) {
        if (booking == null) return null;
        BookingResponse dto = new BookingResponse();
        dto.setId(booking.getId());
        dto.setBookingReference(booking.getBookingReference());
        if (booking.getUser() != null) {
            dto.setUserId(booking.getUser().getId());
            dto.setUserName(booking.getUser().getFullName());
            dto.setUserEmail(booking.getUser().getEmail());
        }
        if (booking.getShow() != null) {
            if (booking.getShow().getMovie() != null) {
                dto.setMovieId(booking.getShow().getMovie().getId());
                dto.setMovieTitle(booking.getShow().getMovie().getTitle());
                dto.setMoviePosterUrl(booking.getShow().getMovie().getPosterUrl());
            }
            if (booking.getShow().getScreen() != null) {
                dto.setScreenName(booking.getShow().getScreen().getName());
                dto.setScreenType(booking.getShow().getScreen().getScreenType());
                if (booking.getShow().getScreen().getTheatre() != null) {
                    dto.setTheatreName(booking.getShow().getScreen().getTheatre().getName());
                    dto.setTheatreCity(booking.getShow().getScreen().getTheatre().getCity());
                    dto.setTheatreAddress(booking.getShow().getScreen().getTheatre().getAddress());
                }
            }
            dto.setShowDate(booking.getShow().getShowDate());
            dto.setStartTime(booking.getShow().getStartTime());
        }
        if (booking.getBookingSeats() != null) {
            dto.setSeatNumbers(booking.getBookingSeats().stream()
                    .map(s -> s.getSeatNumber())
                    .collect(Collectors.toList()));
        } else {
            dto.setSeatNumbers(Collections.emptyList());
        }
        dto.setSeatCount(booking.getSeatCount());
        dto.setTotalAmount(booking.getTotalAmount());
        dto.setStatus(booking.getStatus());
        dto.setPaymentStatus(booking.getPaymentStatus());
        dto.setBookingTime(booking.getBookingTime());
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingReference() {
        return bookingReference;
    }

    public void setBookingReference(String bookingReference) {
        this.bookingReference = bookingReference;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
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

    public String getTheatreAddress() {
        return theatreAddress;
    }

    public void setTheatreAddress(String theatreAddress) {
        this.theatreAddress = theatreAddress;
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

    public List<String> getSeatNumbers() {
        return seatNumbers;
    }

    public void setSeatNumbers(List<String> seatNumbers) {
        this.seatNumbers = seatNumbers;
    }

    public Integer getSeatCount() {
        return seatCount;
    }

    public void setSeatCount(Integer seatCount) {
        this.seatCount = seatCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDateTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalDateTime bookingTime) {
        this.bookingTime = bookingTime;
    }
}
