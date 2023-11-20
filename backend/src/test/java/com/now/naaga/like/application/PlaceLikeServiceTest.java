package com.now.naaga.like.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.MySqlContainerServiceTest;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.application.dto.CheckMyPlaceLikeCommand;
import com.now.naaga.like.application.dto.CountPlaceLikeCommand;
import com.now.naaga.like.domain.MyPlaceLikeType;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.exception.PlaceLikeExceptionType;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.player.domain.Player;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
class PlaceLikeServiceTest extends MySqlContainerServiceTest {

    @Autowired
    private PlaceLikeService placeLikeService;

    @Transactional
    @Test
    void 기존의_좋아요_타입과_반대되는_타입을_등록하면_기존의_엔티티에서_좋아요_타입만_수정된다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        final PlaceLike placeLike = placeLikeBuilder.init()
                                                    .player(player)
                                                    .place(place)
                                                    .placeLikeType(PlaceLikeType.LIKE)
                                                    .build();

        // when
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.DISLIKE);
        final PlaceLike actual = placeLikeService.applyLike(command);

        // then
        SoftAssertions.assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).usingRecursiveComparison()
                          .ignoringFields("placeLikeType")
                          .isEqualTo(placeLike);
            softAssertions.assertThat(actual.getType()).isEqualTo(PlaceLikeType.DISLIKE);
        });
    }

    @Test
    void 기존의_좋아요가_없을_경우에_타입을_등록하면_새로운_좋아요_엔티티가_생성된다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        // when
        final int beforeSize = placeLikeRepository.findAll().size();
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.LIKE);
        placeLikeService.applyLike(command);
        final int afterSize = placeLikeRepository.findAll().size();

        // then
        assertThat(afterSize).isEqualTo(beforeSize + 1);
    }

    @Test
    void 좋아요를_등록하면_좋아요_집계_값에_1_더해진다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        final long beforeLikeCount = 10L;

        placeStatisticsBuilder.init()
                              .place(place)
                              .likeCount(beforeLikeCount)
                              .build();

        // when
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.LIKE);
        placeLikeService.applyLike(command);
        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();

        // then
        assertThat(placeStatistics.getLikeCount()).isEqualTo(beforeLikeCount + 1);
    }

    @Test
    void 기존의_좋아요에서_싫어요로_변경하여_등록하면_좋아요_집계_값을_1_뺀다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        final long beforeLikeCount = 10L;

        placeStatisticsBuilder.init()
                              .place(place)
                              .likeCount(beforeLikeCount)
                              .build();

        placeLikeBuilder.init()
                        .player(player)
                        .place(place)
                        .placeLikeType(PlaceLikeType.LIKE)
                        .build();

        // when
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.DISLIKE);
        placeLikeService.applyLike(command);
        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();

        // then
        assertThat(placeStatistics.getLikeCount()).isEqualTo(beforeLikeCount - 1);
    }

    @Test
    void 새롭게_싫어요를_등록했을_때_좋아요_집계_수는_변하지_않는다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        final long beforeLikeCount = 10L;

        placeStatisticsBuilder.init()
                              .place(place)
                              .likeCount(beforeLikeCount)
                              .build();

        // when
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.DISLIKE);
        placeLikeService.applyLike(command);

        // then
        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(place.getId()).get();
        assertThat(placeStatistics.getLikeCount()).isEqualTo(beforeLikeCount);
    }

    @Test
    void 기존의_좋아요_타입과_같은_타입으로_좋아요_등록을_요청하면_예외가_발생한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeStatisticsBuilder.init()
                              .place(place)
                              .build();

        placeLikeBuilder.init()
                        .player(player)
                        .place(place)
                        .placeLikeType(PlaceLikeType.LIKE)
                        .build();

        // when
        final ApplyLikeCommand command = new ApplyLikeCommand(player.getId(),
                                                              place.getId(),
                                                              PlaceLikeType.LIKE);
        final BaseExceptionType baseExceptionType = assertThrows(PlaceLikeException.class, () ->
                                                                         placeLikeService.applyLike(command)
                                                                ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PlaceLikeExceptionType.ALREADY_APPLIED_TYPE);
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

        // when & then
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

    @Test
    void 특정_장소에_대한_나의_좋아요_여부_조회시_내가_누른_좋아요가_존재한다면_해당_좋아요_타입을_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place place = placeBuilder.init()
                                        .build();

        placeLikeBuilder.init()
                        .player(player)
                        .place(place)
                        .placeLikeType(PlaceLikeType.LIKE)
                        .build();

        // when
        final CheckMyPlaceLikeCommand command = new CheckMyPlaceLikeCommand(player.getId(), place.getId());
        final MyPlaceLikeType actual = placeLikeService.checkMyLike(command);

        // then
        assertThat(actual).isEqualTo(MyPlaceLikeType.LIKE);
    }

    @Test
    void 특정_장소에_대한_나의_좋아요_여부_조회시_내가_누른_좋아요가_없다면_NONE_타입을_반환한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Place applied = placeBuilder.init()
                                          .build();

        final Place notApplied = placeBuilder.init()
                                             .build();

        placeLikeBuilder.init()
                        .player(player)
                        .place(applied)
                        .placeLikeType(PlaceLikeType.LIKE)
                        .build();

        // when
        final CheckMyPlaceLikeCommand command = new CheckMyPlaceLikeCommand(player.getId(), notApplied.getId());
        final MyPlaceLikeType actual = placeLikeService.checkMyLike(command);

        // then
        assertThat(actual).isEqualTo(MyPlaceLikeType.NONE);
    }
}
