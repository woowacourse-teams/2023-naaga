package com.now.naaga.temporaryplace.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.temporaryplace.application.TemporaryPlaceService;
import com.now.naaga.temporaryplace.application.dto.CreateTemporaryPlaceCommand;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.presentation.dto.CreateTemporaryPlaceRequest;
import com.now.naaga.temporaryplace.presentation.dto.TemporaryPlaceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/temporary-places")
@RestController
public class TemporaryPlaceController {

    private final TemporaryPlaceService temporaryPlaceService;

    public TemporaryPlaceController(final TemporaryPlaceService temporaryPlaceService) {
        this.temporaryPlaceService = temporaryPlaceService;
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
}
