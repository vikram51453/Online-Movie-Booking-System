package com.cinemabooking.repository;

import com.cinemabooking.entity.Show;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    List<Show> findAllByOrderByIdDesc();
    List<Show> findByMovieIdAndActiveTrueOrderByShowDateAscStartTimeAsc(Long movieId);

    @Query("SELECT s FROM Show s WHERE s.active = true " +
           "AND (:movieId IS NULL OR s.movie.id = :movieId) " +
           "AND (:theatreId IS NULL OR s.screen.theatre.id = :theatreId) " +
           "AND (:showDate IS NULL OR s.showDate = :showDate) " +
           "ORDER BY s.showDate ASC, s.startTime ASC")
    List<Show> findActiveShows(
            @Param("movieId") Long movieId,
            @Param("theatreId") Long theatreId,
            @Param("showDate") LocalDate showDate
    );

    @Query("SELECT DISTINCT s.showDate FROM Show s WHERE s.movie.id = :movieId AND s.active = true AND s.showDate >= CURRENT_DATE ORDER BY s.showDate ASC")
    List<LocalDate> findAvailableDatesForMovie(@Param("movieId") Long movieId);
}
