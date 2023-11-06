package com.now.naaga.place.repository;

import com.now.naaga.place.domain.PlaceStatistics;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

    Optional<PlaceStatistics> findByPlaceId(Long placeId);
}
