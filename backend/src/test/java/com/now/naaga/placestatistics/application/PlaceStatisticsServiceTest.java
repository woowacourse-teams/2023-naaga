package com.now.naaga.placestatistics.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.application.dto.CreatePlaceStatisticsCommand;
import com.now.naaga.placestatistics.application.dto.SubtractLikeCommand;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
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
