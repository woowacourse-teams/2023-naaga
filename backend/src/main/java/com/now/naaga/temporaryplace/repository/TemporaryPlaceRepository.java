package com.now.naaga.temporaryplace.repository;


import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TemporaryPlaceRepository extends JpaRepository<TemporaryPlace, Long> {

    @Override
    @Query("SELECT tp FROM TemporaryPlace tp JOIN FETCH tp.registeredPlayer")
    List<TemporaryPlace> findAll();
}
