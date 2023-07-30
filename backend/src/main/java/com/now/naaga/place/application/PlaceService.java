package com.now.naaga.place.application;

import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.now.naaga.place.exception.PlaceExceptionType.PLACE_NOT_FOUND;

@Transactional
@Service
public class PlaceService {

    private static final int DISTANCE = 1;

    private final PlaceRepository placeRepository;

    private final PlayerService playerService;

    public PlaceService(final PlaceRepository placeRepository,
                        final PlayerService playerService) {
        this.placeRepository = placeRepository;
        this.playerService = playerService;
    }

    public Place createPlace(final MemberCommand memberCommand,
                             final PlaceCommand placeCommand) {
        //1. 주변 20미터 살펴보기
        Double latitude = placeCommand.latitude();
        Double longitude = placeCommand.longitude();
        Position position = new Position(new BigDecimal(latitude), new BigDecimal(longitude));
        List<Place> places = placeRepository.findPlaceByPositionAndDistance(position, 0.02);

        // TODO: 7/29/23 이 정책이 진짜 필요할까? 
        if (places.size() > 0) {
            throw new RuntimeException("가까운 장소에 이미 등록된 장소가 있습니다.");
        }

        //2. 파일 저장"/Users/hwan/image"
        Path uploadPath = saveImageFile(placeCommand);

        //3. 도메인 생성하기
        Player registeredPlayer = playerService.findPlayerByMemberCommand(memberCommand);
        Place place = new Place(
                placeCommand.name(),
                placeCommand.description(),
                position,
                uploadPath.toString(),
                registeredPlayer
        );
        placeRepository.save(place);

        return place;
    }

    private Path saveImageFile(final PlaceCommand placeCommand) {
        String directory = "/Users/hwan/image";
        MultipartFile imageFile = placeCommand.imageFile();
        System.out.println(imageFile.toString());
        String originalFilename = imageFile.getOriginalFilename();
        System.out.println(originalFilename);
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String savedFilename = UUID.randomUUID() + extension;
        Path uploadPath = Paths.get(directory, savedFilename);

        try {
            imageFile.transferTo(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("파일저장에 실패하였습니다.");
        }
        return uploadPath;
    }

    // TODO: 7/29/23 이젠 장소 추천이란 이름 보단 랜덤, 출제 같은 표현이 적합할듯
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
