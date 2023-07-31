package com.now.naaga.place.presentation;

import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(final PlaceService placeService) {
        this.placeService = placeService;
    }

    @PostMapping("/places")
    public ResponseEntity<PlaceResponse> createPlace(final MemberCommand memberCommand,
                                                     final PlaceCommand placeCommand) {
        System.out.println(placeCommand);
        final Place savedPlace = placeService.createPlace(memberCommand, placeCommand);
        PlaceResponse response = PlaceResponse.from(savedPlace);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }
}
