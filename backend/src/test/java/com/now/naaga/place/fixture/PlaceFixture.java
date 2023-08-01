package com.now.naaga.place.fixture;

import static com.now.naaga.place.fixture.PositionFixture.JEJU_POSITION;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;

import com.now.naaga.place.domain.Place;

public class PlaceFixture {

    public static Place SEOUL_PLACE() {
        return new Place(
                "SEOUL",
                "SEOUL",
                SEOUL_POSITION(),
                "imageUrl", PLAYER());
    }

    public static Place JEJU_PLACE() {
        return new Place(
                "JEJU",
                "JEJU",
                JEJU_POSITION(),
                "imageUrl", PLAYER());
    }
}
