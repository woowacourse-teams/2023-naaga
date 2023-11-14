package com.now.naaga.place.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.place.application.dto.PlusLikeCommand;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
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
class PlaceStatisticsFacadeTest {

    @Autowired
    private PlaceStatisticsFacade placeStatisticsFacade;

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private PlaceStatisticsBuilder placeStatisticsBuilder;

    @Test
    void 동시다발적인_요청으로부터_정확한_좋아요_수_집계를_처리한다() throws InterruptedException {
        // given
        final PlaceStatistics placeStatistics = placeStatisticsBuilder.init()
                                                                      .build();
        final int threadCnt = 50;
        final ExecutorService executorService = Executors.newFixedThreadPool(threadCnt);
        final CountDownLatch countDownLatch = new CountDownLatch(threadCnt);

        // when
        final PlusLikeCommand command = new PlusLikeCommand(1L);
        for (int i = 0; i < threadCnt; i++) {
            executorService.submit(() -> {
                placeStatisticsFacade.plusLike(command);
                countDownLatch.countDown();
            });
        }
        countDownLatch.await();

        // then
        final PlaceStatistics actual = placeStatisticsRepository.findById(placeStatistics.getId()).get();
        assertThat(actual.getLikeCount()).isEqualTo(threadCnt);
    }
}
