package com.now.naaga.placestatistics.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.placestatistics.application.dto.PlusLikeCommand;
import com.now.naaga.placestatistics.application.dto.SubtractLikeCommand;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
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
}
