package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.Game.MAX_HINT_COUNT;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_IN_PROGRESS;

import com.now.naaga.game.exception.GameException;
import com.now.naaga.place.domain.Position;
import com.now.naaga.score.domain.Score;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;

public class ScorePolicyImpl implements ScorePolicy {
    // 거리가 멀수록 점수가 높다/ 시간은 짧을수록 점수가 높다/ 힌트는 사용개수가 적을수록 점수가 높다 / 남은 시도횟수는 많을수록 점수가 높다.
    // 점수에 영향을 미치는 정도 시간> 거리 > 힌트 사용횟수 >  시도횟수
    private static final double DISTANCE_WEIGHT = 300.0;
    private static final double TIME_WEIGHT = 4000.0;
    private static final double HINT_WEIGHT = 50.0;
    private static final double ATTEMPTS_WEIGHT = 50.0;
    
    @Override
    public Score calculate(Game game) {
        validateGameIsOver(game);
        BigDecimal distanceScore = findDistanceInMeters(game).multiply(BigDecimal.valueOf(DISTANCE_WEIGHT));
        BigDecimal timeScore = calculateTimeScore(game).multiply(BigDecimal.valueOf(TIME_WEIGHT));
        BigDecimal hintScore = calculateHintScore(game).multiply(BigDecimal.valueOf(HINT_WEIGHT));
        BigDecimal attemptScore = calculateAttemptScore(game).multiply(BigDecimal.valueOf(ATTEMPTS_WEIGHT));
        BigDecimal totalScore = distanceScore.add(timeScore).add(hintScore).add(attemptScore);
        return new Score((int) totalScore.doubleValue());
    }
    
    private void validateGameIsOver(Game game) {
        if(game.getGameStatus() != GameStatus.DONE) {
            throw new GameException(ALREADY_IN_PROGRESS);
        }
    }
    
    private BigDecimal findDistanceInMeters(Game game) {
        Position startPosition = game.getStartPosition();
        Position destinationPosition = game.getPlace().getPosition();
        return BigDecimal.valueOf(startPosition.calculateDistance(destinationPosition));
    }
    
    private BigDecimal calculateTimeScore(Game game) {
        long durationInSecond = findDurationInSecond(game);
        return BigDecimal.valueOf(1.0)
                .divide(BigDecimal.valueOf(durationInSecond)
                        .divide(BigDecimal.valueOf(60), 5, RoundingMode.HALF_UP),5, RoundingMode.HALF_UP);
    }
    
    private Long findDurationInSecond(Game game) {
        LocalDateTime startTime = game.getStartTime();
        LocalDateTime endTime = game.getEndTime();
        Duration duration = Duration.between(startTime, endTime);
        return duration.toSeconds();
    }
    
    private BigDecimal calculateHintScore(Game game) {
        return BigDecimal.valueOf(MAX_HINT_COUNT - game.getHints().size())
                .divide(BigDecimal.valueOf(MAX_HINT_COUNT), 5, RoundingMode.HALF_UP);
    }
    
    private BigDecimal calculateAttemptScore(Game game) {
        return BigDecimal.valueOf(game.getRemainingAttempts())
                .divide(BigDecimal.valueOf(MAX_ATTEMPT_COUNT), 5, RoundingMode.HALF_UP);
    }
}
