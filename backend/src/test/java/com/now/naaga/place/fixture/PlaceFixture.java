package com.now.naaga.place.fixture;

import com.now.naaga.member.fixture.MemberFixture;
import com.now.naaga.place.application.dto.PlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.fixture.PlayerFixture;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;

public class PlaceFixture {

    public static final Place PLACE = new Place("루터회관", "이곳은 루터회관이다.", new Position(new BigDecimal("37.515411"), new BigDecimal("127.102848")), "이미지 경로", PlayerFixture.PLAYER);

    public static PlaceCommand createDummy() {
        final MultipartFile imageFile;
        try {
            final File file = new File("src/test/java/com/now/naaga/place/fixture/루터회관.png");
            final InputStream inputStream = new FileInputStream(file);
            imageFile = new MockMultipartFile("imageFile", "src/test/java/com/now/naaga/place/fixture/루터회관.png", "image/png", inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new PlaceCommand("루터회관", "이곳은 루터회관이다.", 37.515411, 127.102848, imageFile);
    }
}
