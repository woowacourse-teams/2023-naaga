package com.now.naaga.place.domain;

import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

import static com.now.naaga.place.exception.PlaceExceptionType.CAN_NOT_FIND_PLACE;

@Service
public class PlaceRecommendService {

    private static final int DISTANCE = 1;

    private final PlaceRepository placeRepository;

    public PlaceRecommendService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place recommendRandomPlaceNearBy(final Position position) {
        final List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, DISTANCE);
        if (places.isEmpty()) {
            throw new PlaceException(CAN_NOT_FIND_PLACE);
        }
        return getRandomPlace(places);
    }

    private Place getRandomPlace(final List<Place> places) {
        final Random random = new Random();
        final int randomIndex = random.nextInt();
        return places.get(randomIndex);
    }
}
