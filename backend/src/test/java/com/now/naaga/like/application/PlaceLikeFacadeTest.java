package com.now.naaga.like.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
import com.now.naaga.player.domain.Player;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceLikeFacadeTest {

    @Autowired
    private PlaceLikeFacade placeLikeFacade;

    @Autowired
    private PlaceLikeRepository placeLikeRepository;

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlaceStatisticsBuilder placeStatisticsBuilder;

    @Autowired
    private PlaceLikeBuilder placeLikeBuilder;

    @Test
    void 동시다발적인_좋아요_등록_요청으로부터_정확한_좋아요_수_집계를_처리한다() throws InterruptedException {
        // given
        final Place place = placeBuilder.init()
                                        .build();

        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .place(place)
                                                                      .build();

        final int threadCnt = 20;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            playerBuilder.init()
                         .build();
        }

        // when
        for (int i = 2; i <= threadCnt + 1; i++) {
            final long playerId = i;
            executorService.submit(() -> {
                final ApplyLikeCommand command = new ApplyLikeCommand(playerId, place.getId(), PlaceLikeType.LIKE);
                placeLikeFacade.applyLike(command);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        // then
        final List<PlaceLike> actualPlaceLikes = placeLikeRepository.findAll();
        final PlaceStatistics actualPlaceStatistics = placeStatisticsRepository.findById(placeStatistics.getId()).get();
        assertAll(() -> assertThat(actualPlaceLikes).hasSize(threadCnt),
                  () -> assertThat(actualPlaceStatistics.getLikeCount()).isEqualTo(threadCnt));
    }

    @Test
    void 동시다발적인_좋아요_삭제_요청으로부터_정확한_좋아요_수_집계를_처리한다() throws InterruptedException {
        // given
        final int threadCnt = 20;

        final Place place = placeBuilder.init()
                                        .build();

        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .place(place)
                                                                      .likeCount((long) threadCnt)
                                                                      .build();

        final ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        for (int i = 0; i < threadCnt; i++) {
            final Player player = playerBuilder.init()
                                               .build();
            placeLikeBuilder.init()
                            .placeLikeType(PlaceLikeType.LIKE)
                            .place(place)
                            .player(player)
                            .build();
        }

        // when
        for (int i = 2; i <= threadCnt + 1; i++) {
            final long playerId = i;
            executorService.submit(() -> {
                final CancelLikeCommand command = new CancelLikeCommand(playerId, place.getId());
                placeLikeFacade.cancelLike(command);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        // then
        final List<PlaceLike> actualPlaceLikes = placeLikeRepository.findAll();
        final PlaceStatistics actualPlaceStatistics = placeStatisticsRepository.findById(placeStatistics.getId()).get();
        assertAll(() -> assertThat(actualPlaceLikes).hasSize(0),
                  () -> assertThat(actualPlaceStatistics.getLikeCount()).isEqualTo(0));
    }
}
