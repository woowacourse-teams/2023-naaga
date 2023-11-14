package com.now.naaga.like.application;

import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Component
public class PlaceLikeFacade {

    private final Logger log = LoggerFactory.getLogger(this.getClass().getSimpleName());

    private final PlaceLikeService placeLikeService;

    public PlaceLikeFacade(final PlaceLikeService placeLikeService) {
        this.placeLikeService = placeLikeService;
    }

    public PlaceLike applyLike(final ApplyLikeCommand applyLikeCommand) {
        int retryCnt = 0;
        while (true) {
            try {
                return placeLikeService.applyLike(applyLikeCommand);
            } catch (final OptimisticLockingFailureException e) {
                retryCnt++;
                log.info("낙관적 락 재시도 횟수: {}", retryCnt);
            }
        }
    }

    public void cancelLike(final CancelLikeCommand cancelLikeCommand) {
        int retryCnt = 0;
        while (true) {
            try {
                placeLikeService.cancelLike(cancelLikeCommand);
                break;
            } catch (final OptimisticLockingFailureException e) {
                retryCnt++;
                log.info("낙관적 락 재시도 횟수: {}", retryCnt);
            }
        }
    }
}
