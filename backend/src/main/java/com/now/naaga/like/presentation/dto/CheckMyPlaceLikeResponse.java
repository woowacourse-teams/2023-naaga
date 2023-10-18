package com.now.naaga.like.presentation.dto;

import com.now.naaga.like.domain.MyPlaceLikeType;

public record CheckMyPlaceLikeResponse(MyPlaceLikeType type) {

    public static CheckMyPlaceLikeResponse from(final MyPlaceLikeType myPlaceLikeType) {
        return new CheckMyPlaceLikeResponse(myPlaceLikeType);
    }
}
