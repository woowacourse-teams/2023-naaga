package com.now.naaga.place.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.fixture.PlaceFixture;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
class PlaceStatisticsTest {

    @Test
    void 장소_통계에서_좋아요를_1개_뺀다() {
        // given
        final Place place = PlaceFixture.PLACE();
        final long beforeLikeCount = 10L;
        final PlaceStatistics placeStatistics = new PlaceStatistics(place, beforeLikeCount);

        // when
        placeStatistics.subtractLike();

        // then
        assertThat(placeStatistics.getLikeCount()).isEqualTo(beforeLikeCount - 1);
    }

    @Test
    void 장소_통계_좋아요는_0개_이하로_줄어들지_않는다() {
        // given
        final Place place = PlaceFixture.PLACE();
        final long beforeLikeCount = 0L;
        final PlaceStatistics placeStatistics = new PlaceStatistics(place, beforeLikeCount);

        // when
        placeStatistics.subtractLike();

        // then
        assertThat(placeStatistics.getLikeCount()).isEqualTo(0L);
    }
}
