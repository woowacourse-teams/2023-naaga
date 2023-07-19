package com.now.naaga.place.persistence.repository;

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
            ") * 6371.0 <= :distance")
    List<Place> findPlaceByPositionAndDistance(@Param(value = "user_position") Position position,
                                               @Param(value = "distance") double distance);
}
