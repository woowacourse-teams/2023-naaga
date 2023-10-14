package com.now.naaga.place.service;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.common.fixture.PositionFixture;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import io.restassured.internal.common.assertion.Assertion;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest
class PlaceServiceTest {

    @Autowired
    PlaceService placeService;

    @Autowired
    PlaceBuilder placeBuilder;

    @Transactional
    @RepeatedTest(value = 10)
    void 사용자로부터_300미터_이내의_장소_추천한다() {
        //given
        final Position 현재위치 = PositionFixture.잠실_루터회관_정문_근처_좌표;

        final Position 잠실역교보문고좌표 = PositionFixture.잠실역_교보문고_좌표;
        final Position 잠실루터회관정문좌표 = PositionFixture.잠실_루터회관_정문_좌표;
        final Position 제주좌표 = PositionFixture.제주_좌표;

        final Place 잠실역교보문고 = placeBuilder.init()
                .position(잠실역교보문고좌표)
                .build();
        final Place 잠실루터회관정문 = placeBuilder.init()
                .position(잠실루터회관정문좌표)
                .build();
        final Place 제주 = placeBuilder.init()
                .position(제주좌표)
                .build();

        final RecommendPlaceCommand recommendPlaceCommand = new RecommendPlaceCommand(현재위치);

        //when
        final Place place = placeService.recommendPlaceByPosition(recommendPlaceCommand);

        //then
        assertSoftly(softAssertions -> {
            assertThat(place).usingRecursiveComparison().isNotEqualTo(제주);
            assertThat(place).usingRecursiveComparison().isIn(잠실루터회관정문, 잠실역교보문고);
        });
    }

    @Test
    void 사용자로부터_300미터_이내의_추천할_장소가_없으면_예외를_발생한다() {
        //given
        final Position 제주좌표 = PositionFixture.제주_좌표;

        final Position 잠실역교보문고좌표 = PositionFixture.잠실역_교보문고_좌표;
        final Position 잠실루터회관정문좌표 = PositionFixture.잠실_루터회관_정문_좌표;

        final Place 잠실역교보문고 = placeBuilder.init()
                .position(잠실역교보문고좌표)
                .build();
        final Place 잠실루터회관정문 = placeBuilder.init()
                .position(잠실루터회관정문좌표)
                .build();

        final RecommendPlaceCommand recommendPlaceCommand = new RecommendPlaceCommand(제주좌표);

        // when & then
        assertSoftly(softAssertions -> {
            final BaseExceptionType baseExceptionType = assertThrows(PlaceException.class, () -> placeService.recommendPlaceByPosition(recommendPlaceCommand)).exceptionType();
            assertThat(baseExceptionType).isEqualTo(PlaceExceptionType.NO_EXIST);
        });
    }
}
