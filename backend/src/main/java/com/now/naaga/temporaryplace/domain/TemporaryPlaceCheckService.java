package com.now.naaga.temporaryplace.domain;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            throw new PlaceException(PlaceExceptionType.ALREADY_EXIST_NEARBY);
        }
    }
}
