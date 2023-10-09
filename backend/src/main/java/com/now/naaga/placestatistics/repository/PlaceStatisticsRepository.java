package com.now.naaga.placestatistics.repository;

import com.now.naaga.placestatistics.domain.PlaceStatistics;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<PlaceStatistics> findByPlaceId(Long placeId);

    @Lock(LockModeType.NONE)
    @Override
    PlaceStatistics save(PlaceStatistics placeStatistics);
}
