package com.now.naaga.temporaryplace.application;

import com.now.naaga.common.infrastructure.FileManager;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.application.dto.CreateTemporaryPlaceCommand;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Comparator;
import java.util.List;

@Transactional
@Service
public class TemporaryPlaceService {

    private final TemporaryPlaceRepository temporaryPlaceRepository;

    private final PlayerService playerService;

    private final FileManager<MultipartFile> fileManager;

    public TemporaryPlaceService(final TemporaryPlaceRepository temporaryPlaceRepository,
                                 final PlayerService playerService,
                                 final FileManager<MultipartFile> fileManager) {
        this.temporaryPlaceRepository = temporaryPlaceRepository;
        this.playerService = playerService;
        this.fileManager = fileManager;
    }

    public TemporaryPlace createTemporaryPlace(final CreateTemporaryPlaceCommand createTemporaryPlaceCommand) {
        final Position position = createTemporaryPlaceCommand.position();
        final File uploadPath = fileManager.save(createTemporaryPlaceCommand.imageFile());
        try {
            final Long playerId = createTemporaryPlaceCommand.playerId();
            final Player registeredPlayer = playerService.findPlayerById(playerId);
            final TemporaryPlace temporaryPlace = new TemporaryPlace(
                    createTemporaryPlaceCommand.name(),
                    createTemporaryPlaceCommand.description(),
                    position,
                    fileManager.convertToUrlPath(uploadPath),
                    registeredPlayer);
            return temporaryPlaceRepository.save(temporaryPlace);
        } catch (final RuntimeException exception) {
            uploadPath.delete();
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public List<TemporaryPlace> findAllTemporaryPlace() {
        final List<TemporaryPlace> temporaryPlaces = temporaryPlaceRepository.findAll();
        temporaryPlaces.sort(Comparator.comparing(TemporaryPlace::getCreatedAt).reversed());
        return temporaryPlaces;
    }
}
