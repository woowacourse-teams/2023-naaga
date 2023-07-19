package com.now.naaga.place.application;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.springframework.stereotype.Service;

@Service
public class PlaceService {

    private static final int DISTANCE = 1;

    private final PlaceRepository placeRepository;

    public PlaceService(final PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    // TODO: 2023/07/19 커스텀 예외 만들기
    public Place recommendPlaceByPosition(final Position position) {
        final List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, DISTANCE);
        if (places.isEmpty()) {
            throw new RuntimeException();
        }
        final Random random = new Random();
        final int index = random.nextInt(places.size());
        return places.get(index);
    }
}
