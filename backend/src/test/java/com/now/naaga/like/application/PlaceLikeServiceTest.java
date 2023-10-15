package com.now.naaga.like.application;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.application.dto.CountPlaceLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

import static com.now.naaga.like.exception.PlaceLikeExceptionType.NOT_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceLikeServiceTest {

    private final PlaceLikeService placeLikeService;

    private final PlaceBuilder placeBuilder;

    private final PlayerBuilder playerBuilder;

    private final PlaceLikeBuilder placeLikeBuilder;

    private final PlaceStatisticsBuilder placeStatisticsBuilder;

    private final PlaceLikeRepository placeLikeRepository;

    private final PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    public PlaceLikeServiceTest(final PlaceLikeService placeLikeService,
                                final PlaceBuilder placeBuilder,
                                final PlayerBuilder playerBuilder,
                                final PlaceLikeBuilder placeLikeBuilder,
                                final PlaceStatisticsBuilder placeStatisticsBuilder,
                                final PlaceLikeRepository placeLikeRepository,
                                final PlaceStatisticsRepository placeStatisticsRepository) {
        this.placeLikeService = placeLikeService;
        this.placeBuilder = placeBuilder;
        this.playerBuilder = playerBuilder;
        this.placeLikeBuilder = placeLikeBuilder;
        this.placeStatisticsBuilder = placeStatisticsBuilder;
        this.placeLikeRepository = placeLikeRepository;
        this.placeStatisticsRepository = placeStatisticsRepository;
    }

    @Test
    void 좋아요를_삭제하고_통계에서_좋아요를_1개_뺸다() {
        // given
        final Place place = placeBuilder.init()
                .build();
        final Player player = playerBuilder.init()
                .build();
        final PlaceLike placeLike = placeLikeBuilder.init()
                .place(place)
                .player(player)
                .placeLikeType(PlaceLikeType.LIKE)
                .build();
        final long beforeLikeCount = 10L;
        placeStatisticsBuilder.init()
                .place(place)
                .likeCount(beforeLikeCount)
                .build();
        final CancelLikeCommand cancelLikeCommand = new CancelLikeCommand(player.getId(), place.getId());

        // when
        placeLikeService.cancelLike(cancelLikeCommand);

        // then
        final Optional<PlaceLike> findPlaceLike = placeLikeRepository.findById(placeLike.getId());
        final PlaceStatistics findPlaceStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(findPlaceStatistics.getLikeCount()).isEqualTo(beforeLikeCount - 1);
            softAssertions.assertThat(findPlaceLike).isEmpty();
        });
    }

    @Test
    void 싫어요를_삭제하면_통계가_줄어들지_않는다() {
        // given
        final Place place = placeBuilder.init()
                .build();
        final Player player = playerBuilder.init()
                .build();
        final PlaceLike placeLike = placeLikeBuilder.init()
                .place(place)
                .player(player)
                .placeLikeType(PlaceLikeType.DISLIKE)
                .build();
        final long beforeLikeCount = 10L;
        placeStatisticsBuilder.init()
                .place(place)
                .likeCount(beforeLikeCount)
                .build();
        final CancelLikeCommand cancelLikeCommand = new CancelLikeCommand(player.getId(), place.getId());

        // when
        placeLikeService.cancelLike(cancelLikeCommand);

        // then
        final Optional<PlaceLike> findPlaceLike = placeLikeRepository.findById(placeLike.getId());
        final PlaceStatistics findPlaceStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();
        assertSoftly(softAssertions -> {
            assertThat(findPlaceStatistics.getLikeCount()).isEqualTo(beforeLikeCount);
            assertThat(findPlaceLike).isEmpty();
        });
    }

    @Test
    void 좋아요가_0개일_때_좋아요를_삭제하면_통계에서_좋아요를_0개로_유지한다() {
        // given
        final Place place = placeBuilder.init()
                .build();
        final Player player = playerBuilder.init()
                .build();
        final PlaceLike placeLike = placeLikeBuilder.init()
                .place(place)
                .player(player)
                .placeLikeType(PlaceLikeType.LIKE)
                .build();
        final long beforeLikeCount = 0L;
        placeStatisticsBuilder.init()
                .place(place)
                .likeCount(beforeLikeCount)
                .build();
        final CancelLikeCommand cancelLikeCommand = new CancelLikeCommand(player.getId(), place.getId());

        // when
        placeLikeService.cancelLike(cancelLikeCommand);

        // then
        final Optional<PlaceLike> findPlaceLike = placeLikeRepository.findById(placeLike.getId());
        final PlaceStatistics findPlaceStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(findPlaceStatistics.getLikeCount()).isEqualTo(beforeLikeCount);
            softAssertions.assertThat(findPlaceLike).isEmpty();
        });
    }

    @Test
    void 좋아요를_삭제할_때_좋아요가_존재하지_않으면_아무_일도_일어나지_않는다() {
        // given
        final Place place = placeBuilder.init()
                .build();
        final Player player = playerBuilder.init()
                .build();
        final CancelLikeCommand cancelLikeCommand = new CancelLikeCommand(player.getId(), place.getId());

        // when & then`
        assertDoesNotThrow(() -> placeLikeService.cancelLike(cancelLikeCommand));
    }

    @Test
    void 장소에_대한_좋아요_수를_반환한다() {
        //given
        final Long expected = 123L;
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                .likeCount(expected)
                .build();
        final Long placeId = placeStatistics.getPlace().getId();

        // when
        final CountPlaceLikeCommand countPlaceLikeCommand = new CountPlaceLikeCommand(placeId);
        final Long actual = placeLikeService.countPlaceLike(countPlaceLikeCommand);

        // then
        assertThat(actual).isEqualTo(expected);
    }
}
