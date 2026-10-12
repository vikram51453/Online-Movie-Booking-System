package com.cinemabooking.repository;

import com.cinemabooking.entity.Screen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ScreenRepository extends JpaRepository<Screen, Long> {
    List<Screen> findByTheatreIdOrderByNameAsc(Long theatreId);
}
