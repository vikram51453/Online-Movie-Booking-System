package com.cinemabooking.repository;

import com.cinemabooking.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByActiveTrueOrderByIdDesc();
    List<Movie> findAllByOrderByIdDesc();

    @Query("SELECT m FROM Movie m WHERE m.active = true " +
           "AND (:search IS NULL OR LOWER(m.title) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:genre IS NULL OR LOWER(m.genre) LIKE LOWER(CONCAT('%', :genre, '%'))) " +
           "AND (:language IS NULL OR LOWER(m.language) = LOWER(:language)) " +
           "ORDER BY m.id DESC")
    List<Movie> searchActiveMovies(
            @Param("search") String search,
            @Param("genre") String genre,
            @Param("language") String language
    );

    @Query("SELECT DISTINCT m.genre FROM Movie m WHERE m.active = true")
    List<String> findDistinctGenres();

    @Query("SELECT DISTINCT m.language FROM Movie m WHERE m.active = true")
    List<String> findDistinctLanguages();
}
