package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.gameresult.domain.ResultType;

public record GameResultResponse(Long id,
                                 Long gameId,
                                 GameDestinationResponse destination,
                                 ResultType resultType,
                                 int score,
                                 int totalPlayTime,
                                 int distance,
                                 int hintUses,
                                 int tryCount,
                                 String startTime,
                                 String finishTime) {

    public static GameResultResponse from(final GameRecord gameRecord) {
        return new GameResultResponse(
                gameRecord.gameResult().getId(),
                gameRecord.gameResult().getGame().getId(),
                GameDestinationResponse.from(gameRecord.gameResult().getGame().getPlace()),
                gameRecord.gameResult().getResultType(),
                gameRecord.gameResult().getScore().getValue(),
                gameRecord.durationToInteger(gameRecord.totalPlayTime()),
                gameRecord.distance(),
                gameRecord.hintUses(),
                gameRecord.tryCount(),
                gameRecord.startTime().toString(),
                gameRecord.finishTime().toString());
    }
}
