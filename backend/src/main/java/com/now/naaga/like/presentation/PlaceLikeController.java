package com.now.naaga.like.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.like.application.PlaceLikeService;
import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.presentation.dto.ApplyPlaceLikeRequest;
import com.now.naaga.like.presentation.dto.PlaceLikeResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/places/{placeId}/likes")
@RestController
public class PlaceLikeController {

    private final PlaceLikeService placeLikeService;

    public PlaceLikeController(final PlaceLikeService placeLikeService) {
        this.placeLikeService = placeLikeService;
    }

    @PostMapping
    public ResponseEntity<PlaceLikeResponse> applyPlaceLike(@Auth PlayerRequest playerRequest,
                                                            @PathVariable Long placeId,
                                                            @RequestBody ApplyPlaceLikeRequest applyPlaceLikeRequest) {
        final ApplyLikeCommand command = ApplyLikeCommand.of(playerRequest,
                                                             placeId,
                                                             applyPlaceLikeRequest);
        final PlaceLike placeLike = placeLikeService.applyLike(command);
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
        placeLikeService.cancelLike(cancelLikeCommand);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
