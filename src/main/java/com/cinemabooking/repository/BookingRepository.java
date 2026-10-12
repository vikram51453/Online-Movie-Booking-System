package com.cinemabooking.repository;

import com.cinemabooking.entity.Booking;
import com.cinemabooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Override
    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    Optional<Booking> findById(Long id);

    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    List<Booking> findByUserIdOrderByBookingTimeDesc(Long userId);

    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    List<Booking> findAllByOrderByBookingTimeDesc();

    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    Optional<Booking> findByBookingReference(String bookingReference);

    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    Optional<Booking> findByBookingReferenceAndUserId(String bookingReference, Long userId);

    @EntityGraph(attributePaths = {"bookingSeats", "user", "show", "show.movie", "show.screen", "show.screen.theatre"})
    @Query("SELECT DISTINCT b FROM Booking b WHERE " +
           "(:status IS NULL OR b.status = :status) " +
           "ORDER BY b.bookingTime DESC")
    List<Booking> findFilteredBookings(@Param("status") BookingStatus status);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0) FROM Booking b WHERE b.status = com.cinemabooking.entity.BookingStatus.CONFIRMED")
    BigDecimal sumConfirmedRevenue();

    long countByStatus(BookingStatus status);
}
