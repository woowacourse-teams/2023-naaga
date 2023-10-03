package com.now.naaga.place.application;

import com.now.naaga.common.domain.OrderType;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceRecommendService;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.domain.SortType;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.place.exception.PlaceExceptionType.NO_EXIST;

@Transactional
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final PlaceRecommendService placeRecommendService;

    public PlaceService(final PlaceRepository placeRepository,
                        final PlaceRecommendService placeRecommendService) {
        this.placeRepository = placeRepository;
        this.placeRecommendService = placeRecommendService;
    }

    @Transactional(readOnly = true)
    public List<Place> findAllPlace(final FindAllPlaceCommand findAllPlaceCommand) {
        final List<Place> places = placeRepository.findByRegisteredPlayerId(findAllPlaceCommand.playerId());
        final SortType sortType = findAllPlaceCommand.sortType();
        final OrderType orderType = findAllPlaceCommand.orderType();
        sortType.sort(places, orderType);
        return places;
    }

    @Transactional(readOnly = true)
    public Place findPlaceById(final FindPlaceByIdCommand findPlaceByIdCommand) {
        return placeRepository.findById(findPlaceByIdCommand.placeId())
                .orElseThrow(() -> new PlaceException(NO_EXIST));
    }

    @Transactional(readOnly = true)
    public Place recommendPlaceByPosition(final RecommendPlaceCommand recommendPlaceCommand) {
        final Position position = recommendPlaceCommand.position();
        return placeRecommendService.recommendRandomPlaceNearBy(position);
    }

    public Place createPlace(final CreatePlaceCommand createPlaceCommand) {
        return null;
    }
}
