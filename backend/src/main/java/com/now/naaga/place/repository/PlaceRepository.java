package com.now.naaga.place.repository;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlaceRepository extends JpaRepository<Place, Long> {

    @Query(value = "SELECT place FROM Place place " +
            "WHERE ACOS(" +
            "SIN(RADIANS(:#{#user_position.latitude})) * SIN(RADIANS(place.position.latitude)) " +
            "+ (COS(RADIANS(:#{#user_position.latitude})) * COS(RADIANS(place.position.latitude)) * COS(RADIANS(:#{#user_position.longitude} - place.position.longitude)))" +
            ") * 6371.0 >= :minimum_distance " +
            "AND ACOS(" +
            "SIN(RADIANS(:#{#user_position.latitude})) * SIN(RADIANS(place.position.latitude)) " +
            "+ (COS(RADIANS(:#{#user_position.latitude})) * COS(RADIANS(place.position.latitude)) * COS(RADIANS(:#{#user_position.longitude} - place.position.longitude)))" +
            ") * 6371.0 <= :maximum_distance")
    List<Place> findBetweenRadius(@Param(value = "user_position") final Position position,
                                  @Param(value = "minimum_distance") final double minimumDistance,
                                  @Param(value = "maximum_distance") final double maximumDistance);

    List<Place> findByRegisteredPlayerId(Long playerId);
}
