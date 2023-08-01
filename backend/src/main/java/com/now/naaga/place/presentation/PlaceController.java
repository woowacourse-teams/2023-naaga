package com.now.naaga.place.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(final PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> findAllPlace(@Auth final PlayerRequest playerRequest,
                                                            @RequestParam(name = "sort-by") final String sortBy,
                                                            @RequestParam(name = "order") final String order) {
        final FindAllPlaceCommand findAllPlaceCommand = FindAllPlaceCommand.of(playerRequest, sortBy, order);
        final List<Place> places = placeService.findAllPlace(findAllPlaceCommand);
        final List<PlaceResponse> response = PlaceResponse.convertToPlaceResponses(places);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> findPlaceById(@PathVariable Long placeId) {
        final FindPlaceByIdCommand findPlaceByIdCommand = FindPlaceByIdCommand.from(placeId);
        final Place place = placeService.findPlaceById(findPlaceByIdCommand);
        final PlaceResponse response = PlaceResponse.from(place);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @PostMapping
    public ResponseEntity<PlaceResponse> createPlace(final MemberCommand memberCommand,
                                                     final PlaceCommand placeCommand) {
        final Place savedPlace = placeService.createPlace(memberCommand, placeCommand);
        final PlaceResponse response = PlaceResponse.from(savedPlace);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/places/" + savedPlace.getId()))
                .body(response);
    }
}
