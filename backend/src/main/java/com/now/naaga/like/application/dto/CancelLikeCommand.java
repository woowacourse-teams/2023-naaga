package com.now.naaga.like.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record CancelLikeCommand(Long playerId,
                                Long placeId) {

    public static CancelLikeCommand of(final PlayerRequest playerRequest, final Long placeId) {
        return new CancelLikeCommand(playerRequest.playerId(), placeId);
    }
}
