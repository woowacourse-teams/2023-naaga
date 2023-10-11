package com.now.naaga.like.domain;

public enum PlaceLikeType {

    LIKE,
    DISLIKE,
    ;

    public PlaceLikeType switchType() {
        return this == LIKE ? DISLIKE : LIKE;
    }
}
