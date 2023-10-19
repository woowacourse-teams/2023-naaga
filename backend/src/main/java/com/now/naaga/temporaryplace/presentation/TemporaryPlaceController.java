package com.now.naaga.temporaryplace.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.temporaryplace.application.TemporaryPlaceService;
import com.now.naaga.temporaryplace.application.dto.CreateTemporaryPlaceCommand;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.presentation.dto.CreateTemporaryPlaceRequest;
import com.now.naaga.temporaryplace.presentation.dto.TemporaryPlaceResponse;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/temporary-places")
@RestController
public class TemporaryPlaceController {

    private final TemporaryPlaceService temporaryPlaceService;

    public TemporaryPlaceController(final TemporaryPlaceService temporaryPlaceService) {
        this.temporaryPlaceService = temporaryPlaceService;
    }

    @GetMapping
    public ResponseEntity<List<TemporaryPlaceResponse>> findAllTemporaryPlace() {
        final List<TemporaryPlace> temporaryPlaces = temporaryPlaceService.findAllTemporaryPlace();
        final List<TemporaryPlaceResponse> response = TemporaryPlaceResponse.convertToResponses(temporaryPlaces);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<TemporaryPlaceResponse> createTemporaryPlace(@Auth final PlayerRequest playerRequest,
                                                                       @ModelAttribute final CreateTemporaryPlaceRequest createTemporaryPlaceRequest) {
        final CreateTemporaryPlaceCommand createTemporaryPlaceCommand = CreateTemporaryPlaceCommand.of(playerRequest, createTemporaryPlaceRequest);
        final TemporaryPlace temporaryPlace = temporaryPlaceService.createTemporaryPlace(createTemporaryPlaceCommand);
        final TemporaryPlaceResponse response = TemporaryPlaceResponse.from(temporaryPlace);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/temporary-places/" + temporaryPlace.getId()))
                .body(response);
    }

    @DeleteMapping("/{temporaryPlaceId}")
    public ResponseEntity<Void> deleteTemporaryPlace(@PathVariable final Long temporaryPlaceId) {
        temporaryPlaceService.deleteById(temporaryPlaceId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
    /* S3사용
     */
}
