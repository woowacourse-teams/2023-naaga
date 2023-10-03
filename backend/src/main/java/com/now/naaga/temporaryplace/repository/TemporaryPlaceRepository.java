package com.now.naaga.temporaryplace.repository;


import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TemporaryPlaceRepository extends JpaRepository<TemporaryPlace, Long> {
}
