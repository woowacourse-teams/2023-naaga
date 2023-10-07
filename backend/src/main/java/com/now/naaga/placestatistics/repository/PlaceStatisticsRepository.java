package com.now.naaga.placestatistics.repository;

import com.now.naaga.placestatistics.PlaceStatistics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

    Optional<PlaceStatistics> findByPlaceId(Long placeId);
}
