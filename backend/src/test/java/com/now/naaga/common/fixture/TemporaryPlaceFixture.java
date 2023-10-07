package com.now.naaga.common.fixture;

import com.now.naaga.temporaryplace.domain.TemporaryPlace;

import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;

public class TemporaryPlaceFixture {

    public static final String NAME = "temp_place_name";

    public static final String DESCRIPTION = "temp_place_description";

    public static final String IMAGE_URL = "temp_place_imageUrl";

    public static TemporaryPlace TEMPORARY_PLACE() {
        return new TemporaryPlace(NAME, DESCRIPTION, 잠실_루터회관_정문_좌표, IMAGE_URL, PLAYER());
    }
}
