package com.now.naaga.like.domain;

public enum MyPlaceLikeType {

    LIKE,
    DISLIKE,
    NONE,
    ;

    public static MyPlaceLikeType from(final PlaceLikeType placeLikeType) {
        return MyPlaceLikeType.valueOf(placeLikeType.name());
    }
}
