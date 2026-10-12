package com.cinemabooking.controller;

import com.cinemabooking.dto.ApiResponse;
import com.cinemabooking.dto.ShowRequest;
import com.cinemabooking.dto.ShowResponse;
import com.cinemabooking.dto.ShowSeatLayoutResponse;
import com.cinemabooking.service.ShowService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/shows")
public class ShowController {

    private final ShowService showService;

    public ShowController(ShowService showService) {
        this.showService = showService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShowResponse>>> getShows(
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) Long theatreId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate showDate) {
        List<ShowResponse> shows = showService.getShows(movieId, theatreId, showDate);
        return ResponseEntity.ok(ApiResponse.ok(shows));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShowResponse>> getShowById(@PathVariable Long id) {
        ShowResponse show = showService.getShowById(id);
        return ResponseEntity.ok(ApiResponse.ok(show));
    }

    @GetMapping("/dates/{movieId}")
    public ResponseEntity<ApiResponse<List<LocalDate>>> getAvailableDates(@PathVariable Long movieId) {
        List<LocalDate> dates = showService.getAvailableDatesForMovie(movieId);
        return ResponseEntity.ok(ApiResponse.ok(dates));
    }

    @GetMapping("/{id}/seats")
    public ResponseEntity<ApiResponse<ShowSeatLayoutResponse>> getShowSeatLayout(@PathVariable Long id) {
        ShowSeatLayoutResponse layout = showService.getSeatLayoutForShow(id);
        return ResponseEntity.ok(ApiResponse.ok(layout));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShowResponse>> createShow(@Valid @RequestBody ShowRequest request) {
        ShowResponse response = showService.createShow(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Show created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShowResponse>> updateShow(
            @PathVariable Long id,
            @Valid @RequestBody ShowRequest request) {
        ShowResponse response = showService.updateShow(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Show updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShow(@PathVariable Long id) {
        showService.deleteShow(id);
        return ResponseEntity.ok(ApiResponse.ok("Show deactivated successfully", null));
    }
}
