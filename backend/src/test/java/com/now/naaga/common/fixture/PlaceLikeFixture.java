package com.now.naaga.common.fixture;

import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;

public class PlaceLikeFixture {

    public static PlaceLike PLACE_LIKE() {
        return new PlaceLike(PlaceFixture.PLACE(), PlayerFixture.PLAYER(), PlaceLikeType.LIKE);
    }
}
