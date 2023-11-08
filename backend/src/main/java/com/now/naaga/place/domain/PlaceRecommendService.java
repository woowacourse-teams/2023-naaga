package com.now.naaga.place.domain;

import static com.now.naaga.place.exception.PlaceExceptionType.NO_EXIST;

import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.repository.PlaceRepository;
import java.util.List;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class PlaceRecommendService {

    private static final double MINIMUM_DISTANCE = 0.15;
    private static final double MAXIMUM_DISTANCE = 0.5;

    private final PlaceRepository placeRepository;

    public PlaceRecommendService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public Place recommendRandomPlaceNearBy(final Position position) {
        final List<Place> places = placeRepository.findBetweenRadius(position, MINIMUM_DISTANCE, MAXIMUM_DISTANCE);
        if (places.isEmpty()) {
            throw new PlaceException(NO_EXIST);
        }
        return getRandomPlace(places);
    }

    private Place getRandomPlace(final List<Place> places) {
        final Random random = new Random();
        final int randomIndex = random.nextInt(places.size());
        return places.get(randomIndex);
    }
}
