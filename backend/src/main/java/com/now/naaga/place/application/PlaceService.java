package com.now.naaga.place.application;

import com.now.naaga.common.infrastructure.MultipartFileManager;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceCheckService;
import com.now.naaga.place.domain.Position;
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

import static com.now.naaga.place.exception.PlaceExceptionType.PLACE_NOT_FOUND;

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

    public Place createPlace(final MemberCommand memberCommand,
                             final PlaceCommand placeCommand) {
        final Position position = Position.of(placeCommand.latitude(), placeCommand.longitude());
        placeCheckService.checkOtherPlaceNearby(position);

        final Path uploadPath = saveImageFile(placeCommand.imageFile());

        final Player registeredPlayer = playerService.findPlayerByMember(memberCommand);
        final Place place = new Place(placeCommand.name(), placeCommand.description(), position, uploadPath.toString(), registeredPlayer);

        placeRepository.save(place);
        return place;
    }

    private Path saveImageFile(final MultipartFile imageFile) {
        // TODO: 8/1/23 경로 prod, dev, test,... 환경별로 따로 두기
        final String directory = "/Users/hwan/image";
        final MultipartFileManager multipartFileManager = new MultipartFileManager();
        return multipartFileManager.save(imageFile, directory);
        // TODO: 8/1/23 rollback되면 파일을 삭제해야되는데... 뭔가 이상하다
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
