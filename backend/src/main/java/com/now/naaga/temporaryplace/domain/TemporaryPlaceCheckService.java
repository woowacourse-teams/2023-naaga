package com.now.naaga.temporaryplace.domain;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.temporaryplace.exception.TemporaryPlaceException;
import com.now.naaga.temporaryplace.exception.TemporaryPlaceExceptionType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class TemporaryPlaceCheckService {

    private final PlaceRepository placeRepository;

    public TemporaryPlaceCheckService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Transactional(readOnly = true)
    public void checkOtherPlaceNearby(final Position position) {
        List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, 0.02);
        if (!places.isEmpty()) {
            throw new TemporaryPlaceException(TemporaryPlaceExceptionType.ALREADY_EXIST_NEARBY);
        }
    }
}
