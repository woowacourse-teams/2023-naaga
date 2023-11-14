package com.now.naaga.place.application;

import com.now.naaga.place.application.dto.PlusLikeCommand;
import com.now.naaga.place.application.dto.SubtractLikeCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class PlaceStatisticsFacade {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final PlaceStatisticsService placeStatisticsService;

    public PlaceStatisticsFacade(final PlaceStatisticsService placeStatisticsService) {
        this.placeStatisticsService = placeStatisticsService;
    }

    public void plusLike(final PlusLikeCommand plusLikeCommand) {
        int retryCnt = 0;
        while (true) {
            try {
                placeStatisticsService.plusLike(plusLikeCommand);
                break;
            } catch (final OptimisticLockingFailureException e) {
                retryCnt++;
                log.info("낙관적 락 재시도 횟수: {}", retryCnt);
            }
        }
    }

    public void subtractLike(final SubtractLikeCommand subtractLikeCommand) {
        int retryCnt = 0;
        while (true) {
            try {
                placeStatisticsService.subtractLike(subtractLikeCommand);
                break;
            } catch (final OptimisticLockingFailureException e) {
                retryCnt++;
                log.info("낙관적 락 재시도 횟수: {}", retryCnt);
            }
        }
    }
}
