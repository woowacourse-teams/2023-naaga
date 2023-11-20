package com.now.naaga.place.repository;

import com.now.naaga.place.domain.PlaceStatistics;
import jakarta.persistence.LockModeType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceStatisticsRepository extends JpaRepository<PlaceStatistics, Long> {

    Optional<PlaceStatistics> findByPlaceId(final Long placeId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ps FROM PlaceStatistics ps WHERE ps.place.id = :placeId")
    Optional<PlaceStatistics> findByPlaceIdForUpdate(@Param("placeId") final Long placeId);
}
