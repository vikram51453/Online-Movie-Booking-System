package com.cinemabooking.controller;

import com.cinemabooking.dto.*;
import com.cinemabooking.entity.BookingStatus;
import com.cinemabooking.service.BookingService;
import com.cinemabooking.service.MovieService;
import com.cinemabooking.service.ShowService;
import com.cinemabooking.service.TheatreService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final BookingService bookingService;
    private final MovieService movieService;
    private final TheatreService theatreService;
    private final ShowService showService;

    public AdminController(BookingService bookingService,
                           MovieService movieService,
                           TheatreService theatreService,
                           ShowService showService) {
        this.bookingService = bookingService;
        this.movieService = movieService;
        this.theatreService = theatreService;
        this.showService = showService;
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsResponse>> getStats() {
        DashboardStatsResponse stats = bookingService.getDashboardStats();
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping("/bookings")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getAllBookings(
            @RequestParam(required = false) BookingStatus status) {
        List<BookingResponse> bookings = bookingService.getAllBookingsAdmin(status);
        return ResponseEntity.ok(ApiResponse.ok(bookings));
    }

    @GetMapping("/movies")
    public ResponseEntity<ApiResponse<List<MovieResponse>>> getAllMovies() {
        List<MovieResponse> movies = movieService.getAllMoviesAdmin();
        return ResponseEntity.ok(ApiResponse.ok(movies));
    }

    @GetMapping("/theatres")
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getAllTheatres() {
        List<TheatreResponse> theatres = theatreService.getAllTheatresAdmin();
        return ResponseEntity.ok(ApiResponse.ok(theatres));
    }

    @GetMapping("/shows")
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getAllShows() {
        List<ShowResponse> shows = showService.getAllShowsAdmin();
        return ResponseEntity.ok(ApiResponse.ok(shows));
    }
}
