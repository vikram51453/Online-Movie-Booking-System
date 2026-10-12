package com.cinemabooking.service;

import com.cinemabooking.dto.ScreenRequest;
import com.cinemabooking.dto.ScreenResponse;
import com.cinemabooking.dto.TheatreRequest;
import com.cinemabooking.dto.TheatreResponse;
import com.cinemabooking.entity.*;
import com.cinemabooking.exception.ResourceNotFoundException;
import com.cinemabooking.repository.ScreenRepository;
import com.cinemabooking.repository.SeatRepository;
import com.cinemabooking.repository.TheatreRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TheatreService {

    private final TheatreRepository theatreRepository;
    private final ScreenRepository screenRepository;
    private final SeatRepository seatRepository;

    public TheatreService(TheatreRepository theatreRepository,
                          ScreenRepository screenRepository,
                          SeatRepository seatRepository) {
        this.theatreRepository = theatreRepository;
        this.screenRepository = screenRepository;
        this.seatRepository = seatRepository;
    }

    public List<TheatreResponse> getAllActiveTheatres() {
        return theatreRepository.findByActiveTrueOrderByNameAsc().stream()
                .map(TheatreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<TheatreResponse> getAllTheatresAdmin() {
        return theatreRepository.findAllByOrderByIdDesc().stream()
                .map(TheatreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public TheatreResponse getTheatreById(Long id) {
        Theatre theatre = getTheatreEntity(id);
        return TheatreResponse.fromEntity(theatre);
    }

    public Theatre getTheatreEntity(Long id) {
        return theatreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Theatre not found with ID: " + id));
    }

    public List<TheatreResponse> getTheatresShowingMovie(Long movieId) {
        return theatreRepository.findTheatresShowingMovie(movieId).stream()
                .map(TheatreResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public TheatreResponse createTheatre(TheatreRequest request) {
        Theatre theatre = new Theatre();
        theatre.setName(request.getName().trim());
        theatre.setCity(request.getCity().trim());
        theatre.setAddress(request.getAddress().trim());
        theatre.setTotalScreens(request.getTotalScreens() != null ? request.getTotalScreens() : 1);
        theatre.setActive(request.isActive());
        Theatre saved = theatreRepository.save(theatre);
        return TheatreResponse.fromEntity(saved);
    }

    @Transactional
    public TheatreResponse updateTheatre(Long id, TheatreRequest request) {
        Theatre theatre = getTheatreEntity(id);
        theatre.setName(request.getName().trim());
        theatre.setCity(request.getCity().trim());
        theatre.setAddress(request.getAddress().trim());
        theatre.setTotalScreens(request.getTotalScreens() != null ? request.getTotalScreens() : theatre.getTotalScreens());
        theatre.setActive(request.isActive());
        Theatre saved = theatreRepository.save(theatre);
        return TheatreResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteTheatre(Long id) {
        Theatre theatre = getTheatreEntity(id);
        theatre.setActive(false);
        theatreRepository.save(theatre);
    }

    public List<ScreenResponse> getScreensByTheatre(Long theatreId) {
        return screenRepository.findByTheatreIdOrderByNameAsc(theatreId).stream()
                .map(ScreenResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public Screen getScreenEntity(Long id) {
        return screenRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Screen not found with ID: " + id));
    }

    @Transactional
    public ScreenResponse createScreenWithSeats(ScreenRequest request) {
        Theatre theatre = getTheatreEntity(request.getTheatreId());

        int rows = (request.getRowsCount() != null && request.getRowsCount() > 0) ? request.getRowsCount() : 8;
        int cols = (request.getColsCount() != null && request.getColsCount() > 0) ? request.getColsCount() : 10;
        int totalSeats = rows * cols;

        Screen screen = new Screen();
        screen.setTheatre(theatre);
        screen.setName(request.getName().trim());
        screen.setScreenType(request.getScreenType() != null ? request.getScreenType().trim() : "Standard 2D");
        screen.setRowsCount(rows);
        screen.setColsCount(cols);
        screen.setTotalSeats(totalSeats);

        Screen savedScreen = screenRepository.save(screen);

        // Generate Seat grid
        List<Seat> seats = new ArrayList<>();
        char startRowChar = 'A';
        for (int r = 0; r < rows; r++) {
            String rowName = String.valueOf((char) (startRowChar + r));
            SeatType type;
            if (r < 2) {
                type = SeatType.SILVER;
            } else if (r < rows - 2) {
                type = SeatType.GOLD;
            } else {
                type = SeatType.PLATINUM;
            }

            for (int c = 1; c <= cols; c++) {
                Seat seat = new Seat(savedScreen, rowName, c, type);
                seats.add(seat);
            }
        }
        seatRepository.saveAll(seats);

        // Update theatre screen count if needed
        List<Screen> currentScreens = screenRepository.findByTheatreIdOrderByNameAsc(theatre.getId());
        theatre.setTotalScreens(currentScreens.size());
        theatreRepository.save(theatre);

        return ScreenResponse.fromEntity(savedScreen);
    }
}
