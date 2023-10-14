package com.now.naaga.temporaryplace.application;

import com.now.naaga.common.infrastructure.AwsS3FileManager;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.application.dto.CreateTemporaryPlaceCommand;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.exception.TemporaryPlaceException;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

import static com.now.naaga.temporaryplace.exception.TemporaryPlaceExceptionType.NOT_EXIST;

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
            awsS3FileManager.deleteFile(imageUrl);
            throw exception;
        }
    }
    
    public void deleteById(final Long id) {
        TemporaryPlace temporaryPlace = temporaryPlaceRepository.findById(id)
                                                                .orElseThrow(() -> new TemporaryPlaceException(NOT_EXIST));
        temporaryPlaceRepository.deleteById(id);
        awsS3FileManager.deleteFile(temporaryPlace.getImageUrl());
    }
    
    @Transactional(readOnly = true)
    public List<TemporaryPlace> findAllTemporaryPlace() {
        final List<TemporaryPlace> temporaryPlaces = temporaryPlaceRepository.findAll();
        temporaryPlaces.sort(Comparator.comparing(TemporaryPlace::getCreatedAt).reversed());
        return temporaryPlaces;
    }
}
