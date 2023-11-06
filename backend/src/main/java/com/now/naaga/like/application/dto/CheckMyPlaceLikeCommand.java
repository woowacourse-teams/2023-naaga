package com.now.naaga.like.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record CheckMyPlaceLikeCommand(Long playerId,
                                      Long placeId) {

    public static CheckMyPlaceLikeCommand of(final PlayerRequest playerRequest,
                                             final Long placeId) {
        return new CheckMyPlaceLikeCommand(playerRequest.playerId(),
                                           placeId);
    }
}
