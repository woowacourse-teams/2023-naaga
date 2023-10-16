package com.now.naaga.like.application.dto;

import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.presentation.dto.ApplyPlaceLikeRequest;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record ApplyLikeCommand(Long playerId,
                               Long placeId,
                               PlaceLikeType placeLikeType) {

    public static ApplyLikeCommand of(final PlayerRequest playerRequest,
                                      final Long placeId,
                                      final ApplyPlaceLikeRequest applyPlaceLikeRequest) {
        return new ApplyLikeCommand(playerRequest.playerId(), placeId, applyPlaceLikeRequest.placeLikeType());
    }
}
