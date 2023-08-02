package com.now.naaga.place.fixture;

import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.presentation.dto.CoordinateResponse;
import com.now.naaga.place.presentation.dto.PlaceResponse;
import com.now.naaga.player.fixture.PlayerFixture;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;

import static com.now.naaga.place.fixture.PositionFixture.JEJU_POSITION;
import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;

public class PlaceFixture {

    public static Place SEOUL_PLACE() {
        return new Place(
                "SEOUL",
                "SEOUL",
                PositionFixture.SEOUL_POSITION(),
                "imageUrl",
                PLAYER());
    }

    public static Place JEJU_PLACE() {
        return new Place(
                "JEJU",
                "JEJU",
                JEJU_POSITION(),
                "imageUrl",
                PLAYER());
    }

    public static CreatePlaceCommand CREATE_SEOUL_PLACE() {
        final MultipartFile imageFile;
        try {
            final File file = new File("src/test/java/com/now/naaga/place/fixture/루터회관.png");
            final InputStream inputStream = new FileInputStream(file);
            System.out.println("---");
            System.out.println(inputStream);
            imageFile = new MockMultipartFile("imageFile", "src/test/java/com/now/naaga/place/fixture/루터회관.png", "image/png", inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new CreatePlaceCommand(1L, "SEOUL", "SEOUL", PositionFixture.SEOUL_POSITION(), imageFile);
    }

    public static final PlaceResponse PLACE_RESPONSE = new PlaceResponse(null, "루터회관", new CoordinateResponse(37.515411, 127.102848), "/Users/hwan/image/루터회관.png", "이곳은 루터회관이다.");
}
