package com.now.naaga.temporaryplace.application;

import com.now.naaga.common.infrastructure.AwsS3FileManager;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.application.dto.CreateTemporaryPlaceCommand;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Transactional
@Service
public class TemporaryPlaceService {

    private final TemporaryPlaceRepository temporaryPlaceRepository;

    private final PlayerService playerService;

    private final AwsS3FileManager awsS3FileManager;

    public TemporaryPlaceService(final TemporaryPlaceRepository temporaryPlaceRepository,
                                 final PlayerService playerService,
                                 final AwsS3FileManager awsS3FileManager) {
        this.temporaryPlaceRepository = temporaryPlaceRepository;
        this.playerService = playerService;
        this.awsS3FileManager = awsS3FileManager;
    }

    public TemporaryPlace createTemporaryPlace(final CreateTemporaryPlaceCommand createTemporaryPlaceCommand) {
        final Position position = createTemporaryPlaceCommand.position();
        String imageUrl = awsS3FileManager.uploadFile(createTemporaryPlaceCommand.imageFile());

        try {
            final Long playerId = createTemporaryPlaceCommand.playerId();
            final Player registeredPlayer = playerService.findPlayerById(playerId);
            final TemporaryPlace temporaryPlace = new TemporaryPlace(
                    createTemporaryPlaceCommand.name(),
                    createTemporaryPlaceCommand.description(),
                    position,
                    imageUrl,
                    registeredPlayer);
            return temporaryPlaceRepository.save(temporaryPlace);
        } catch (final RuntimeException exception) {
            //todo: s3에서 이미지 롤백(혹은 삭제)
            throw exception;
        }
    }

    public void deleteById(final Long id) {
        temporaryPlaceRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<TemporaryPlace> findAllTemporaryPlace() {
        final List<TemporaryPlace> temporaryPlaces = temporaryPlaceRepository.findAll();
        temporaryPlaces.sort(Comparator.comparing(TemporaryPlace::getCreatedAt).reversed());
        return temporaryPlaces;
    }
}
