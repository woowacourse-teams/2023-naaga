package com.now.naaga.placestatistics.application;

import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.application.dto.FindPlaceStatisticsByPlaceIdCommand;
import com.now.naaga.placestatistics.application.dto.SubtractLikeCommand;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.exception.PlaceStatisticsException;
import com.now.naaga.placestatistics.exception.PlaceStatisticsExceptionType;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceStatisticsServiceTest {

    PlaceStatisticsService placeStatisticsService;

    PlaceStatisticsBuilder placeStatisticsBuilder;

    PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    public PlaceStatisticsServiceTest(final PlaceStatisticsService placeStatisticsService,
                                      final PlaceStatisticsBuilder placeStatisticsBuilder,
                                      final PlaceStatisticsRepository placeStatisticsRepository) {
        this.placeStatisticsService = placeStatisticsService;
        this.placeStatisticsBuilder = placeStatisticsBuilder;
        this.placeStatisticsRepository = placeStatisticsRepository;
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
