package com.now.naaga.place.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.ServiceTest;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.common.fixture.PositionFixture;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import java.util.Optional;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
class PlaceServiceTest extends ServiceTest {

    @Autowired
    private PlaceService placeService;

    @Transactional
    @Test
    void 장소를_등록한_뒤_기존의_검수_장소_데이터는_삭제한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long temporaryPlaceId = temporaryPlace.getId();

        final CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand("루터회관",
                                                                             "이곳은 루터회관이다 알겠냐",
                                                                             Position.of(1.23, 4.56),
                                                                             "image/url",
                                                                             player.getId(),
                                                                             temporaryPlaceId);

        // when
        final Place actual = placeService.createPlace(createPlaceCommand);

        // then
        final Place expected = new Place(createPlaceCommand.name(),
                                         createPlaceCommand.description(),
                                         createPlaceCommand.position(),
                                         createPlaceCommand.imageUrl(),
                                         player);

        final TemporaryPlace found = temporaryPlaceRepository.findById(temporaryPlaceId)
                                                             .orElse(null);

        assertAll(
                () -> assertThat(actual).usingRecursiveComparison()
                                        .ignoringExpectedNullFields()
                                        .isEqualTo(expected),
                () -> assertThat(found).isNull()
                 );
    }

    @Test
    void 장소_등록_시_장소_통계도_함께_등록된다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long temporaryPlaceId = temporaryPlace.getId();

        final CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand("루터회관",
                                                                             "이곳은 루터회관이다 알겠냐",
                                                                             Position.of(1.23, 4.56),
                                                                             "image/url",
                                                                             player.getId(),
                                                                             temporaryPlaceId);

        // when
        final Place place = placeService.createPlace(createPlaceCommand);

        // then
        final Optional<PlaceStatistics> placeStatistics = placeStatisticsRepository.findByPlaceId(place.getId());
        assertAll(
                () -> assertThat(placeStatistics).isNotEmpty()
                 );
    }

    @Transactional
    @RepeatedTest(value = 10)
    void 사용자로부터_150미터_500미터_사이의_장소_추천한다() {
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
    void 사용자로부터_150미터_500미터_사이에_추천할_장소가_없으면_예외를_발생한다() {
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
