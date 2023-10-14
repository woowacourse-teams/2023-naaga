package com.now.naaga.placestatistics.application;

import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.place.domain.Place;
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

import static org.assertj.core.api.Assertions.assertThat;

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
}
