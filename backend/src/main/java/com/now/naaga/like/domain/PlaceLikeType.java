package com.now.naaga.like.domain;

public enum PlaceLikeType {

    LIKE,
    DISLIKE,
    ;

    public PlaceLikeType switchType() {
        if (this == LIKE) {
            return DISLIKE;
        }
        return LIKE;
    }
}
