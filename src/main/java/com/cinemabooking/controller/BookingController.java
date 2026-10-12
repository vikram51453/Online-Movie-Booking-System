package com.cinemabooking.controller;

import com.cinemabooking.dto.ApiResponse;
import com.cinemabooking.dto.BookingRequest;
import com.cinemabooking.dto.BookingResponse;
import com.cinemabooking.entity.User;
import com.cinemabooking.service.AuthService;
import com.cinemabooking.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final AuthService authService;

    public BookingController(BookingService bookingService, AuthService authService) {
        this.bookingService = bookingService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<BookingResponse>> createBooking(@Valid @RequestBody BookingRequest request) {
        User user = authService.getCurrentUser();
        BookingResponse response = bookingService.createBooking(request, user);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Booking confirmed successfully", response));
    }

    @GetMapping("/my")
    public ResponseEntity<ApiResponse<List<BookingResponse>>> getMyBookings() {
        User user = authService.getCurrentUser();
        List<BookingResponse> responses = bookingService.getUserBookings(user);
        return ResponseEntity.ok(ApiResponse.ok(responses));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingById(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        BookingResponse response = bookingService.getBookingById(id, user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping("/reference/{reference}")
    public ResponseEntity<ApiResponse<BookingResponse>> getBookingByReference(@PathVariable String reference) {
        User user = authService.getCurrentUser();
        BookingResponse response = bookingService.getBookingByReference(reference, user);
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<BookingResponse>> cancelBooking(@PathVariable Long id) {
        User user = authService.getCurrentUser();
        BookingResponse response = bookingService.cancelBooking(id, user);
        return ResponseEntity.ok(ApiResponse.ok("Booking cancelled and refunded successfully", response));
    }
}
