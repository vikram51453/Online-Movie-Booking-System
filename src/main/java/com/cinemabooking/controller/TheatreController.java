package com.cinemabooking.controller;

import com.cinemabooking.dto.*;
import com.cinemabooking.service.TheatreService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/theatres")
public class TheatreController {

    private final TheatreService theatreService;

    public TheatreController(TheatreService theatreService) {
        this.theatreService = theatreService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getAllTheatres() {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getAllActiveTheatres()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TheatreResponse>> getTheatreById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getTheatreById(id)));
    }

    @GetMapping("/for-movie/{movieId}")
    public ResponseEntity<ApiResponse<List<TheatreResponse>>> getTheatresForMovie(@PathVariable Long movieId) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getTheatresShowingMovie(movieId)));
    }

    @GetMapping("/{theatreId}/screens")
    public ResponseEntity<ApiResponse<List<ScreenResponse>>> getScreens(@PathVariable Long theatreId) {
        return ResponseEntity.ok(ApiResponse.ok(theatreService.getScreensByTheatre(theatreId)));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TheatreResponse>> createTheatre(@Valid @RequestBody TheatreRequest request) {
        TheatreResponse response = theatreService.createTheatre(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Theatre created successfully", response));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<TheatreResponse>> updateTheatre(
            @PathVariable Long id,
            @Valid @RequestBody TheatreRequest request) {
        TheatreResponse response = theatreService.updateTheatre(id, request);
        return ResponseEntity.ok(ApiResponse.ok("Theatre updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteTheatre(@PathVariable Long id) {
        theatreService.deleteTheatre(id);
        return ResponseEntity.ok(ApiResponse.ok("Theatre deactivated successfully", null));
    }

    @PostMapping("/screens")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ScreenResponse>> createScreen(@Valid @RequestBody ScreenRequest request) {
        ScreenResponse response = theatreService.createScreenWithSeats(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Screen and seat layout generated successfully", response));
    }
}
