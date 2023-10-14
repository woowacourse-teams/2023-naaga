package com.now.naaga.like.presentation.dto;

import com.now.naaga.like.domain.PlaceLikeType;

public record CheckMyPlaceLikeResponse(PlaceLikeType type) {

    public static CheckMyPlaceLikeResponse from(final PlaceLikeType placeLikeType) {
        return new CheckMyPlaceLikeResponse(placeLikeType);
    }
}
