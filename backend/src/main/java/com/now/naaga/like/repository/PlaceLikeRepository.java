package com.now.naaga.like.repository;

import com.now.naaga.like.domain.PlaceLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

    Optional<PlaceLike> findByPlaceIdAndPlayerId(Long placeId,
                                                Long playerId);
}
