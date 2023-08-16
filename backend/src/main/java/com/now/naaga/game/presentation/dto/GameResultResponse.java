package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.domain.ResultType;

import java.time.LocalDateTime;

public record GameResultResponse(Long id,
                                 Long gameId,
                                 GameDestinationResponse destination,
                                 ResultType resultType,
                                 int score,
                                 int totalPlayTime,
                                 int distance,
                                 int hintUses,
                                 int tryCount,
                                 LocalDateTime startTime,
                                 LocalDateTime finishTime) {

    public static GameResultResponse from(final GameRecord gameRecord) {
        return new GameResultResponse(
                gameRecord.getGameResult().getId(),
                gameRecord.getGameResult().getGame().getId(),
                GameDestinationResponse.from(gameRecord.getGameResult().getGame().getPlace()),
                gameRecord.getGameResult().getResultType(),
                gameRecord.getGameResult().getScore().getValue(),
                gameRecord.durationToInteger(gameRecord.getTotalPlayTime()),
                gameRecord.getDistance(),
                gameRecord.getHintUses(),
                gameRecord.getTryCount(),
                gameRecord.getStartTime(),
                gameRecord.getFinishTime());
    }
}
