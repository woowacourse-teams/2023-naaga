package com.now.naaga.like.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.like.application.PlaceLikeFacade;
import com.now.naaga.like.application.PlaceLikeService;
import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.application.dto.CheckMyPlaceLikeCommand;
import com.now.naaga.like.application.dto.CountPlaceLikeCommand;
import com.now.naaga.like.domain.MyPlaceLikeType;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.presentation.dto.ApplyPlaceLikeRequest;
import com.now.naaga.like.presentation.dto.CheckMyPlaceLikeResponse;
import com.now.naaga.like.presentation.dto.PlaceLikeCountResponse;
import com.now.naaga.like.presentation.dto.PlaceLikeResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/places/{placeId}/likes")
@RestController
public class PlaceLikeController {

    private final PlaceLikeFacade placeLikeFacade;

    private final PlaceLikeService placeLikeService;

    public PlaceLikeController(final PlaceLikeFacade placeLikeFacade,
                               final PlaceLikeService placeLikeService) {
        this.placeLikeFacade = placeLikeFacade;
        this.placeLikeService = placeLikeService;
    }

    @PostMapping
    public ResponseEntity<PlaceLikeResponse> applyPlaceLike(@Auth PlayerRequest playerRequest,
                                                            @PathVariable Long placeId,
                                                            @RequestBody ApplyPlaceLikeRequest applyPlaceLikeRequest) {
        final ApplyLikeCommand command = ApplyLikeCommand.of(playerRequest,
                                                             placeId,
                                                             applyPlaceLikeRequest);
        final PlaceLike placeLike = placeLikeFacade.applyLike(command);
        final PlaceLikeResponse response = PlaceLikeResponse.from(placeLike);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/places/" + placeId + "/likes/my"))
                .body(response);
    }

    @DeleteMapping("/my")
    public ResponseEntity<Void> cancelPlaceLike(@Auth PlayerRequest playerRequest,
                                                @PathVariable Long placeId) {
        final CancelLikeCommand cancelLikeCommand = CancelLikeCommand.of(playerRequest, placeId);
        placeLikeFacade.cancelLike(cancelLikeCommand);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/my")
    public ResponseEntity<CheckMyPlaceLikeResponse> checkMyPlaceLike(@Auth final PlayerRequest playerRequest,
                                                                     @PathVariable final Long placeId) {
        final CheckMyPlaceLikeCommand command = CheckMyPlaceLikeCommand.of(playerRequest, placeId);
        final MyPlaceLikeType myPlaceLikeType = placeLikeService.checkMyLike(command);
        final CheckMyPlaceLikeResponse response = CheckMyPlaceLikeResponse.from(myPlaceLikeType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @GetMapping("/count")
    public ResponseEntity<PlaceLikeCountResponse> countPlaceLike(@PathVariable Long placeId) {
        final CountPlaceLikeCommand countPlaceLikeCommand = new CountPlaceLikeCommand(placeId);
        final Long placeLikeCount = placeLikeService.countPlaceLike(countPlaceLikeCommand);

        final PlaceLikeCountResponse placeLikeCountResponse = new PlaceLikeCountResponse(placeLikeCount);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(placeLikeCountResponse);
    }
}
