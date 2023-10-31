package com.now.naaga.like.repository;

import com.now.naaga.like.domain.PlaceLike;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceLikeRepository extends JpaRepository<PlaceLike, Long> {

    Optional<PlaceLike> findByPlaceIdAndPlayerId(final Long placeId,
                                                final Long playerId);
}
