package com.now.naaga.place.persistence.repository;

import com.now.naaga.place.domain.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
