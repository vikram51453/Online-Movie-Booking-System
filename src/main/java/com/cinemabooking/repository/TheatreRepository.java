package com.cinemabooking.repository;

import com.cinemabooking.entity.Theatre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TheatreRepository extends JpaRepository<Theatre, Long> {
    List<Theatre> findByActiveTrueOrderByNameAsc();
    List<Theatre> findAllByOrderByIdDesc();
    List<Theatre> findByCityIgnoreCaseAndActiveTrue(String city);

    @Query("SELECT DISTINCT t.city FROM Theatre t WHERE t.active = true ORDER BY t.city ASC")
    List<String> findDistinctCities();

    @Query("SELECT DISTINCT s.screen.theatre FROM Show s WHERE s.movie.id = :movieId AND s.active = true AND s.screen.theatre.active = true")
    List<Theatre> findTheatresShowingMovie(@Param("movieId") Long movieId);
}
