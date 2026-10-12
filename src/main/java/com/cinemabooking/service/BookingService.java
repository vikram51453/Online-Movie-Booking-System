package com.cinemabooking.service;

import com.cinemabooking.dto.BookingRequest;
import com.cinemabooking.dto.BookingResponse;
import com.cinemabooking.dto.DashboardStatsResponse;
import com.cinemabooking.entity.*;
import com.cinemabooking.exception.BadRequestException;
import com.cinemabooking.exception.ResourceNotFoundException;
import com.cinemabooking.exception.SeatAlreadyBookedException;
import com.cinemabooking.repository.*;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingSeatRepository bookingSeatRepository;
    private final ShowRepository showRepository;
    private final SeatRepository seatRepository;
    private final MovieRepository movieRepository;
    private final TheatreRepository theatreRepository;
    private final UserRepository userRepository;

    public BookingService(BookingRepository bookingRepository,
                          BookingSeatRepository bookingSeatRepository,
                          ShowRepository showRepository,
                          SeatRepository seatRepository,
                          MovieRepository movieRepository,
                          TheatreRepository theatreRepository,
                          UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.bookingSeatRepository = bookingSeatRepository;
        this.showRepository = showRepository;
        this.seatRepository = seatRepository;
        this.movieRepository = movieRepository;
        this.theatreRepository = theatreRepository;
        this.userRepository = userRepository;
    }

    @Transactional(isolation = Isolation.READ_COMMITTED)
    public BookingResponse createBooking(BookingRequest request, User user) {
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new BadRequestException("Please select at least one seat to book");
        }

        Show show = showRepository.findById(request.getShowId())
                .orElseThrow(() -> new ResourceNotFoundException("Show not found with ID: " + request.getShowId()));

        if (!show.isActive()) {
            throw new BadRequestException("This show is no longer active for booking");
        }

        // Check if any seat is already booked for this show
        Set<Long> alreadyBookedSeatIds = bookingSeatRepository.findBookedSeatIdsForShow(show.getId(), BookingStatus.CONFIRMED);
        for (Long seatId : request.getSeatIds()) {
            if (alreadyBookedSeatIds.contains(seatId)) {
                throw new SeatAlreadyBookedException("One or more selected seats are already booked. Please choose other seats.");
            }
        }

        // Retrieve and validate seats
        List<Seat> selectedSeats = seatRepository.findAllById(request.getSeatIds());
        if (selectedSeats.size() != request.getSeatIds().size()) {
            throw new BadRequestException("One or more seat IDs are invalid");
        }

        Long screenId = show.getScreen().getId();
        for (Seat seat : selectedSeats) {
            if (!seat.getScreen().getId().equals(screenId)) {
                throw new BadRequestException("Seat " + seat.getSeatCode() + " does not belong to this show's auditorium");
            }
        }

        // Authoritatively calculate ticket prices on the backend
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<BookingSeat> bookingSeats = new ArrayList<>();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShow(show);
        booking.setBookingReference(generateBookingReference());
        booking.setSeatCount(selectedSeats.size());
        booking.setStatus(BookingStatus.CONFIRMED);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setBookingTime(LocalDateTime.now());

        for (Seat seat : selectedSeats) {
            BigDecimal seatPrice = show.getBasePrice()
                    .multiply(BigDecimal.valueOf(seat.getSeatType().getMultiplier()))
                    .setScale(2, RoundingMode.HALF_UP);
            totalAmount = totalAmount.add(seatPrice);

            BookingSeat bookingSeat = new BookingSeat(
                    booking,
                    show,
                    seat,
                    seat.getSeatCode(),
                    seatPrice
            );
            bookingSeats.add(bookingSeat);
        }

        booking.setTotalAmount(totalAmount);
        booking.setBookingSeats(bookingSeats);

        try {
            Booking savedBooking = bookingRepository.save(booking);
            return BookingResponse.fromEntity(savedBooking);
        } catch (DataIntegrityViolationException ex) {
            // Caught if a concurrent transaction booked one of the seats simultaneously
            throw new SeatAlreadyBookedException("One or more selected seats were just reserved by another customer. Please select different seats.");
        }
    }

    public List<BookingResponse> getUserBookings(User user) {
        return bookingRepository.findByUserIdOrderByBookingTimeDesc(user.getId()).stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public BookingResponse getBookingByReference(String bookingReference, User user) {
        Booking booking = bookingRepository.findByBookingReference(bookingReference)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with reference: " + bookingReference));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isAdmin && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to view this booking");
        }

        return BookingResponse.fromEntity(booking);
    }

    public BookingResponse getBookingById(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isAdmin && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to view this booking");
        }

        return BookingResponse.fromEntity(booking);
    }

    @Transactional
    public BookingResponse cancelBooking(Long id, User user) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with ID: " + id));

        boolean isAdmin = user.getRole() == Role.ROLE_ADMIN;
        if (!isAdmin && !booking.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("You do not have permission to cancel this booking");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BadRequestException("This booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setPaymentStatus(PaymentStatus.REFUNDED);

        // Remove seat reservations so seats are immediately released for other customers
        booking.getBookingSeats().clear();

        Booking updated = bookingRepository.save(booking);
        return BookingResponse.fromEntity(updated);
    }

    public List<BookingResponse> getAllBookingsAdmin(BookingStatus status) {
        return bookingRepository.findFilteredBookings(status).stream()
                .map(BookingResponse::fromEntity)
                .collect(Collectors.toList());
    }

    public DashboardStatsResponse getDashboardStats() {
        long totalMovies = movieRepository.count();
        long totalTheatres = theatreRepository.count();
        long totalShows = showRepository.count();
        long totalBookings = bookingRepository.countByStatus(BookingStatus.CONFIRMED);
        BigDecimal totalRevenue = bookingRepository.sumConfirmedRevenue();
        long totalCustomers = userRepository.countByRole(Role.ROLE_CUSTOMER);

        return new DashboardStatsResponse(
                totalMovies,
                totalTheatres,
                totalShows,
                totalBookings,
                totalRevenue != null ? totalRevenue : BigDecimal.ZERO,
                totalCustomers
        );
    }

    private String generateBookingReference() {
        String datePart = DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDate.now());
        String randomPart = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "MBK-" + datePart + "-" + randomPart;
    }
}
