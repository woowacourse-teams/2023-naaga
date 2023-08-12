package com.now.naaga.place.application;

import static com.now.naaga.place.exception.PlaceExceptionType.NO_EXIST;

import com.now.naaga.common.domain.OrderType;
import com.now.naaga.common.infrastructure.FileManager;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceCheckService;
import com.now.naaga.place.domain.PlaceRecommendService;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.domain.SortType;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
public class PlaceService {

    private final PlaceRepository placeRepository;

    private final PlayerService playerService;

    private final PlaceCheckService placeCheckService;

    private final PlaceRecommendService placeRecommendService;

    private final FileManager<MultipartFile> fileManager;

    public PlaceService(final PlaceRepository placeRepository,
                        final PlayerService playerService,
                        final PlaceCheckService placeCheckService,
                        final PlaceRecommendService placeRecommendService,
                        final FileManager<MultipartFile> fileManager) {
        this.placeRepository = placeRepository;
        this.playerService = playerService;
        this.placeCheckService = placeCheckService;
        this.placeRecommendService = placeRecommendService;
        this.fileManager = fileManager;
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
        final Position position = createPlaceCommand.position();
        placeCheckService.checkOtherPlaceNearby(position);
        final File uploadPath = fileManager.save(createPlaceCommand.imageFile());
        try {
            final Player registeredPlayer = playerService.findPlayerByMemberId(1L);
            final Place place = new Place(
                    createPlaceCommand.name(),
                    createPlaceCommand.description(),
                    position,
                    fileManager.convertToUrlPath(uploadPath),
                    registeredPlayer);
            placeRepository.save(place);
            return place;
        } catch (final RuntimeException exception) {
            uploadPath.delete();
            throw exception;
        }
    }
}
