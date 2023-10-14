package com.now.naaga.like.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.like.application.PlaceLikeService;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.application.dto.CheckMyPlaceLikeCommand;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.presentation.dto.CheckMyPlaceLikeResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/places/{placeId}/likes")
@RestController
public class PlaceLikeController {

    private final PlaceLikeService placeLikeService;

    public PlaceLikeController(final PlaceLikeService placeLikeService) {
        this.placeLikeService = placeLikeService;
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

    @GetMapping("/my")
    public ResponseEntity<CheckMyPlaceLikeResponse> checkMyPlaceLike(@Auth final PlayerRequest playerRequest,
                                                                     @PathVariable final Long placeId) {
        final CheckMyPlaceLikeCommand command = CheckMyPlaceLikeCommand.of(playerRequest, placeId);
        final PlaceLikeType placeLikeType = placeLikeService.checkMyLike(command);
        final CheckMyPlaceLikeResponse response = CheckMyPlaceLikeResponse.from(placeLikeType);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
}
