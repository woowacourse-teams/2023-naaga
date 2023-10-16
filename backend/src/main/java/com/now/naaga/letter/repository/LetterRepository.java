package com.now.naaga.letter.repository;

import com.now.naaga.letter.domain.Letter;
import com.now.naaga.place.domain.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LetterRepository extends JpaRepository<Letter, Long> {

    @Query(value = "SELECT letter FROM Letter letter " +
            "WHERE ACOS(" +
            "SIN(RADIANS(:#{#user_position.latitude})) * SIN(RADIANS(letter.position.latitude)) " +
            "+ (COS(RADIANS(:#{#user_position.latitude})) * COS(RADIANS(letter.position.latitude)) * COS(RADIANS(:#{#user_position.longitude} - letter.position.longitude)))" +
            ") * 6371.0 <= :distance")
    List<Letter> findLetterByPositionAndDistance(@Param(value = "user_position") final Position position,
                                                 @Param(value = "distance") final double distance);
}
