package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.PositionFixture.서울_좌표;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;

public class PlaceFixture {

    public static final String NAME = "place_name";

    public static final String DESCRIPTION = "place_description";

    public static final String IMAGE_URL = "place_imageUrl";

    public static Place PLACE() {
        return new Place(NAME, DESCRIPTION, 서울_좌표, IMAGE_URL, null);
    }

    public static Place PLACE(final Position position, final Player player) {
        return new Place(NAME, DESCRIPTION, position, IMAGE_URL, player);
    }
}
