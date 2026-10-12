package com.cinemabooking.repository;

import com.cinemabooking.entity.BookingSeat;
import com.cinemabooking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {

    @Query("SELECT bs FROM BookingSeat bs WHERE bs.show.id = :showId AND bs.booking.status = :status")
    List<BookingSeat> findByShowIdAndBookingStatus(@Param("showId") Long showId, @Param("status") BookingStatus status);

    @Query("SELECT bs.seat.id FROM BookingSeat bs WHERE bs.show.id = :showId AND bs.booking.status = :status")
    Set<Long> findBookedSeatIdsForShow(@Param("showId") Long showId, @Param("status") BookingStatus status);

    @Query("SELECT COUNT(bs) > 0 FROM BookingSeat bs WHERE bs.show.id = :showId AND bs.seat.id IN :seatIds AND bs.booking.status = :status")
    boolean existsByShowIdAndSeatIdInAndBookingStatus(
            @Param("showId") Long showId,
            @Param("seatIds") List<Long> seatIds,
            @Param("status") BookingStatus status
    );

    void deleteByBookingId(Long bookingId);
}
