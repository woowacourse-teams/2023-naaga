package com.now.naaga.place.domain;

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
        if (places.size() > 0) {
            throw new RuntimeException("가까운 장소에 이미 등록된 장소가 있습니다.");
        }
    }
}
