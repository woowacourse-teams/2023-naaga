package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.Game.MAX_HINT_COUNT;

import com.now.naaga.score.domain.Score;
import java.time.Duration;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ScorePolicyImpl implements ScorePolicy {
    
    private static final Score BASE_SCORE = new Score(50);
    private static final double HINT_SCORE_RATIO = 0.3;
    private static final double ATTEMPT_SCORE_RATIO = 0.3;
    private static final double AVERAGE_SPEED = 10 / 9;
    
    @Override
    public Score calculate(final Game game) {
        return BASE_SCORE
                .plus(calculateHintScore(game))
                .plus(calculateAttemptScore(game))
                .plus(calculateTimeScore(game));
    }
    
    private Score calculateHintScore(final Game game) {
        final int usedHintCount = game.getHints().size();
        final double maxHintScore = BASE_SCORE.getValue() * HINT_SCORE_RATIO;
        final double hintScoreValue = maxHintScore - ((maxHintScore / MAX_HINT_COUNT) * usedHintCount);
        return new Score((int) hintScoreValue);
    }
    
    private Score calculateAttemptScore(final Game game) {
        final int remainingAttempt = game.getRemainingAttempts();
        final int maxPossibleAttempts = MAX_ATTEMPT_COUNT - 1;
        final double maxAttemptScore = BASE_SCORE.getValue() * ATTEMPT_SCORE_RATIO;
        final double attemptScoreValue = maxAttemptScore / maxPossibleAttempts * remainingAttempt;
        return new Score((int) attemptScoreValue);
    }
    
    private Score calculateTimeScore(final Game game) {
        final double distance = game.findDistance();
        final long playTimeInSecond = findDurationInSecond(game);
        final double slope = AVERAGE_SPEED / (distance * BASE_SCORE.getValue());
        final double shiftX = distance / AVERAGE_SPEED;
        final double timeScoreValue = 1 / (slope * (playTimeInSecond + shiftX));
        return new Score((int) timeScoreValue);
    }
    
    private long findDurationInSecond(Game game) {
        LocalDateTime startTime = game.getStartTime();
        LocalDateTime endTime = game.getEndTime();
        Duration duration = Duration.between(startTime, endTime);
        return duration.toSeconds();
    }
}
