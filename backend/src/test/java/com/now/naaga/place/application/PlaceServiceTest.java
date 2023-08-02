package com.now.naaga.place.application;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.fixture.PlaceFixture;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private PlaceService placeService;

    @Test
    void 장소를_저장한다() {
        //when
        Place acutual = placeService.createPlace(PlaceFixture.CREATE_SEOUL_PLACE());
        //then
        assertThat(PlaceFixture.SEOUL_PLACE())
                .usingRecursiveComparison()
                .ignoringFields("id", "position")
                .isEqualTo(acutual);
    }
}
