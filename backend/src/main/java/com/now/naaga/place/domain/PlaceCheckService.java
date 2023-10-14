package com.now.naaga.place.domain;

import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class PlaceCheckService {

    private final PlaceRepository placeRepository;

    public PlaceCheckService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Transactional(readOnly = true)
    public void checkOtherPlaceNearby(final Position position) {
        List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, 0.02);
        if (!places.isEmpty()) {
            throw new PlaceException(PlaceExceptionType.ALREADY_EXIST_NEARBY);
        }
    }
}
