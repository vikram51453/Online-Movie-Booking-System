package com.cinemabooking.service;

import com.cinemabooking.dto.SeatDetailDto;
import com.cinemabooking.dto.ShowRequest;
import com.cinemabooking.dto.ShowResponse;
import com.cinemabooking.dto.ShowSeatLayoutResponse;
import com.cinemabooking.entity.*;
import com.cinemabooking.exception.BadRequestException;
import com.cinemabooking.exception.ResourceNotFoundException;
import com.cinemabooking.repository.BookingSeatRepository;
import com.cinemabooking.repository.SeatRepository;
import com.cinemabooking.repository.ShowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final MovieService movieService;
    private final TheatreService theatreService;
    private final SeatRepository seatRepository;
    private final BookingSeatRepository bookingSeatRepository;

    public ShowService(ShowRepository showRepository,
                       MovieService movieService,
                       TheatreService theatreService,
                       SeatRepository seatRepository,
                       BookingSeatRepository bookingSeatRepository) {
        this.showRepository = showRepository;
        this.movieService = movieService;
        this.theatreService = theatreService;
        this.seatRepository = seatRepository;
        this.bookingSeatRepository = bookingSeatRepository;
    }

    public List<ShowResponse> getShows(Long movieId, Long theatreId, LocalDate showDate) {
        return showRepository.findActiveShows(movieId, theatreId, showDate).stream()
                .map(ShowResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ShowResponse> getAllShowsAdmin() {
        return showRepository.findAllByOrderByIdDesc().stream()
                .map(ShowResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public ShowResponse getShowById(Long id) {
        return ShowResponse.fromEntity(getShowEntity(id));
    }

    public Show getShowEntity(Long id) {
        return showRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + id));
    }

    public List<LocalDate> getAvailableDatesForMovie(Long movieId) {
        return showRepository.findAvailableDatesForMovie(movieId);
    }

    @Transactional
    public ShowResponse createShow(ShowRequest request) {
        Movie movie = movieService.getMovieEntity(request.getMovieId());
        Screen screen = theatreService.getScreenEntity(request.getScreenId());

        if (request.getBasePrice().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("Base price must be greater than zero");
        }

        Show show = new Show();
        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(request.getShowDate());
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime() != null ? request.getEndTime() :
                request.getStartTime().plusMinutes(movie.getDurationMinutes() + 15));
        show.setBasePrice(request.getBasePrice());
        show.setActive(request.isActive());

        Show saved = showRepository.save(show);
        return ShowResponse.fromEntity(saved);
    }

    @Transactional
    public ShowResponse updateShow(Long id, ShowRequest request) {
        Show show = getShowEntity(id);
        Movie movie = movieService.getMovieEntity(request.getMovieId());
        Screen screen = theatreService.getScreenEntity(request.getScreenId());

        show.setMovie(movie);
        show.setScreen(screen);
        show.setShowDate(request.getShowDate());
        show.setStartTime(request.getStartTime());
        show.setEndTime(request.getEndTime());
        show.setBasePrice(request.getBasePrice());
        show.setActive(request.isActive());

        Show saved = showRepository.save(show);
        return ShowResponse.fromEntity(saved);
    }

    @Transactional
    public void deleteShow(Long id) {
        Show show = getShowEntity(id);
        show.setActive(false);
        showRepository.save(show);
    }

    public ShowSeatLayoutResponse getSeatLayoutForShow(Long showId) {
        Show show = getShowEntity(showId);
        Screen screen = show.getScreen();

        List<Seat> allSeats = seatRepository.findByScreenIdOrderByRowNameAscSeatNumberAsc(screen.getId());
        Set<Long> bookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShow(showId, BookingStatus.CONFIRMED);

        List<SeatDetailDto> seatDtos = allSeats.stream().map(seat -> {
            BigDecimal seatPrice = show.getBasePrice()
                    .multiply(BigDecimal.valueOf(seat.getSeatType().getMultiplier()))
                    .setScale(2, RoundingMode.HALF_UP);
            boolean isBooked = bookedSeatIds.contains(seat.getId());
            return new SeatDetailDto(
                    seat.getId(),
                    seat.getRowName(),
                    seat.getSeatNumber(),
                    seat.getSeatCode(),
                    seat.getSeatType(),
                    seatPrice,
                    isBooked
            );
        }).collect(Collectors.toList());

        ShowSeatLayoutResponse response = new ShowSeatLayoutResponse();
        response.setShowId(show.getId());
        response.setMovieId(show.getMovie().getId());
        response.setMovieTitle(show.getMovie().getTitle());
        response.setMoviePosterUrl(show.getMovie().getPosterUrl());
        response.setTheatreId(screen.getTheatre().getId());
        response.setTheatreName(screen.getTheatre().getName());
        response.setTheatreCity(screen.getTheatre().getCity());
        response.setScreenId(screen.getId());
        response.setScreenName(screen.getName());
        response.setScreenType(screen.getScreenType());
        response.setRowsCount(screen.getRowsCount());
        response.setColsCount(screen.getColsCount());
        response.setShowDate(show.getShowDate());
        response.setStartTime(show.getStartTime());
        response.setBasePrice(show.getBasePrice());
        response.setSeats(seatDtos);

        return response;
    }
}
