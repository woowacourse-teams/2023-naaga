package com.now.naaga.place.application;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static com.now.naaga.place.exception.PlaceExceptionType.PLACE_NOT_FOUND;

@Transactional
@Service
public class PlaceService {

    private static final int DISTANCE = 1;

    private final PlaceRepository placeRepository;

    public PlaceService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    @Transactional(readOnly = true)
    public Place recommendPlaceByPosition(final Position position) {
        final List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, DISTANCE);
        if (places.isEmpty()) {
            throw new PlaceException(PLACE_NOT_FOUND);
        }
        return places.get(getRandomIndex(places));
    }

    private int getRandomIndex(final List<Place> places) {
        final Random random = new Random();
        return random.nextInt(places.size());
    }
}
