package com.now.naaga.like.presentation.dto;

import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;

public record PlaceLikeResponse(Long id,
                                Long playerId,
                                Long placeId,
                                PlaceLikeType type) {

    public static PlaceLikeResponse from(final PlaceLike placeLike) {
        return new PlaceLikeResponse(placeLike.getId(),
                                     placeLike.getPlayer().getId(),
                                     placeLike.getPlace().getId(),
                                     placeLike.getType());
    }
}
