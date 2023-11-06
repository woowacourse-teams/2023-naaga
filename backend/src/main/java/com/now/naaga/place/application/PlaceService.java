package com.now.naaga.place.application;

import static com.now.naaga.place.exception.PlaceExceptionType.NO_EXIST;

import com.now.naaga.common.domain.OrderType;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceCreateEvent;
import com.now.naaga.place.domain.PlaceRecommendService;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.domain.SortType;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.repository.PlaceRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import java.util.List;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final PlayerService playerService;

    private final PlaceRecommendService placeRecommendService;

    private final ApplicationEventPublisher applicationEventPublisher;

    public PlaceService(final PlaceRepository placeRepository,
                        final PlayerService playerService,
                        final PlaceRecommendService placeRecommendService,
                        final ApplicationEventPublisher applicationEventPublisher) {
        this.placeRepository = placeRepository;
        this.playerService = playerService;
        this.placeRecommendService = placeRecommendService;
        this.applicationEventPublisher = applicationEventPublisher;
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
        final Long registeredPlayerId = createPlaceCommand.registeredPlayerId();
        final Player registeredPlayer = playerService.findPlayerById(registeredPlayerId);
        final Place place = new Place(createPlaceCommand.name(),
                                      createPlaceCommand.description(),
                                      createPlaceCommand.position(),
                                      createPlaceCommand.imageUrl(),
                                      registeredPlayer);

        placeRepository.save(place);
        applicationEventPublisher.publishEvent(new PlaceCreateEvent(createPlaceCommand.temporaryPlaceId(), place.getId()));
        return place;
    }
}
