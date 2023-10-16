package com.now.naaga.place.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.application.dto.FindAllPlaceCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.presentation.dto.CreatePlaceRequest;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/places")
@RestController
public class PlaceController {

    private final PlaceService placeService;

    public PlaceController(final PlaceService placeService) {
        this.placeService = placeService;
    }

    @GetMapping
    public ResponseEntity<List<PlaceResponse>> findAllPlace(@Auth final PlayerRequest playerRequest,
                                                            @RequestParam(name = "sort-by", defaultValue = "TIME") final String sortBy,
                                                            @RequestParam(name = "order", defaultValue = "DESCENDING") final String order) {
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
    public ResponseEntity<PlaceResponse> createPlace(@RequestBody final CreatePlaceRequest createPlaceRequest) {
        final CreatePlaceCommand createPlaceCommand = CreatePlaceCommand.from(createPlaceRequest);
        final Place place = placeService.createPlace(createPlaceCommand);
        final PlaceResponse response = PlaceResponse.from(place);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/places/" + place.getId()))
                .body(response);
    }
}
