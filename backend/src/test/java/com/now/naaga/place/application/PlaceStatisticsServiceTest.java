package com.now.naaga.place.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.place.application.dto.CreatePlaceStatisticsCommand;
import com.now.naaga.place.application.dto.FindPlaceStatisticsByPlaceIdCommand;
import com.now.naaga.place.application.dto.PlusLikeCommand;
import com.now.naaga.place.application.dto.SubtractLikeCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.place.exception.PlaceStatisticsException;
import com.now.naaga.place.exception.PlaceStatisticsExceptionType;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceStatisticsServiceTest {

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private PlaceStatisticsService placeStatisticsService;

    @Autowired
    private PlaceStatisticsBuilder placeStatisticsBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Transactional
    @Test
    void 장소통계를_생성한다() {
        // given
        final Place place = placeBuilder.init()
                                        .build();

        // when
        final CreatePlaceStatisticsCommand command = new CreatePlaceStatisticsCommand(place.getId());
        final PlaceStatistics actual = placeStatisticsService.createPlaceStatistics(command);

        // then
        final PlaceStatistics expected = new PlaceStatistics(place, PlaceStatistics.LIKE_COUNT_DEFAULT_VALUE);
        assertThat(actual).usingRecursiveComparison()
                          .ignoringExpectedNullFields()
                          .isEqualTo(expected);
    }

    @Test
    void 장소통계에서_좋아요_수_1개를_올린다() {
        // given
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .likeCount(5L)
                                                                      .build();

        // when
        placeStatisticsService.plusLike(new PlusLikeCommand(placeStatistics.getPlace().getId()));

        // then
        final PlaceStatistics actual = placeStatisticsRepository.findByPlaceId(placeStatistics.getPlace().getId()).get();
        assertThat(actual.getLikeCount()).isEqualTo(6L);
    }

    @Test
    void 장소통계의_좋아요_수를_올릴때_장소통계를_찾을_수_없다면_장소_예외가_발생한다() {
        // given
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .build();

        // when
        final PlusLikeCommand plusLikeCommand = new PlusLikeCommand(placeStatistics.getPlace().getId() + 1);
        final BaseExceptionType baseExceptionType = assertThrows(PlaceException.class,
                                                                 () -> placeStatisticsService.plusLike(plusLikeCommand)
                                                                ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PlaceExceptionType.NO_EXIST);
    }

    @Test
    void 장소통계_1개를_줄인다() {
        //given
        final long beforeLikeCount = 10L;
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .likeCount(beforeLikeCount)
                                                                      .build();
        final Place place = placeStatistics.getPlace();

        //when
        final SubtractLikeCommand subtractLikeCommand = new SubtractLikeCommand(place.getId());
        placeStatisticsService.subtractLike(subtractLikeCommand);

        //then
        final PlaceStatistics findPlaceStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();
        assertThat(findPlaceStatistics.getLikeCount()).isEqualTo(beforeLikeCount - 1);
    }

    @Transactional
    @Test
    void 장소통계를_장소아이디로_조회한다() {
        // given
        final PlaceStatistics expected = placeStatisticsBuilder.init()
                                                               .likeCount(123L)
                                                               .build();

        // when
        final Long placeId = expected.getPlace().getId();
        final FindPlaceStatisticsByPlaceIdCommand findPlaceStatisticsByPlaceIdCommand = new FindPlaceStatisticsByPlaceIdCommand(placeId);
        final PlaceStatistics actual = placeStatisticsService.findPlaceStatisticsByPlaceId(findPlaceStatisticsByPlaceIdCommand);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expected);
    }

    @Test
    void 장소통계를_장소아이디로_조회_시_없으면_예외를_발생한다() {
        // given & when
        final FindPlaceStatisticsByPlaceIdCommand findPlaceStatisticsByPlaceIdCommand = new FindPlaceStatisticsByPlaceIdCommand(1L);

        // then
        assertAll(() -> {
            final BaseExceptionType baseExceptionType = assertThrows(PlaceStatisticsException.class, () -> placeStatisticsService.findPlaceStatisticsByPlaceId(findPlaceStatisticsByPlaceIdCommand))
                    .exceptionType();
            assertThat(baseExceptionType).isEqualTo(PlaceStatisticsExceptionType.NOT_FOUND);
        });
    }
}
