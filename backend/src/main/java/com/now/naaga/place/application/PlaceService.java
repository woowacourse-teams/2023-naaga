package com.now.naaga.place.application;

import com.now.naaga.common.domain.OrderType;
import com.now.naaga.common.infrastructure.MultipartFileManager;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceCheckService;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.domain.SortType;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Random;

import static com.now.naaga.place.exception.PlaceExceptionType.CAN_NOT_FIND_PLACE;
import static com.now.naaga.place.exception.PlaceExceptionType.NO_EXIST;

@Transactional
@Service
public class PlaceService {

    private static final int DISTANCE = 1;

    private final PlayerService playerService;

    private final PlaceCheckService placeCheckService;

    private final PlaceRepository placeRepository;

    public PlaceService(final PlayerService playerService,
                        final PlaceCheckService placeCheckService,
                        final PlaceRepository placeRepository) {
        this.playerService = playerService;
        this.placeCheckService = placeCheckService;
        this.placeRepository = placeRepository;
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
    public Place recommendPlaceByPosition(final Position position) {
        final List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, DISTANCE);
        if (places.isEmpty()) {
            throw new PlaceException(CAN_NOT_FIND_PLACE);
        }
        return places.get(getRandomIndex(places));
    }

    private int getRandomIndex(final List<Place> places) {
        final Random random = new Random();
        return random.nextInt(places.size());
    }

    public Place createPlace(final MemberCommand memberCommand,
                             final PlaceCommand placeCommand) {
        final Position position = Position.of(placeCommand.latitude(), placeCommand.longitude());
        placeCheckService.checkOtherPlaceNearby(position);
        final Path uploadPath = saveImageFile(placeCommand.imageFile());
        final Player registeredPlayer = playerService.findPlayerByMemberId(1L);
        final Place place = new Place(placeCommand.name(), placeCommand.description(), position, uploadPath.toString(), registeredPlayer);
        placeRepository.save(place);
        return place;
    }

    private Path saveImageFile(final MultipartFile imageFile) {
        final MultipartFileManager multipartFileManager = new MultipartFileManager();
        return multipartFileManager.save(imageFile);
    }
}
