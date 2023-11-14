package com.now.naaga.place.repository;

import com.now.naaga.place.domain.PlaceStatistics;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<PlaceStatistics> findByPlaceId(Long placeId);
}
