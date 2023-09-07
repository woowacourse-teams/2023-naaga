package com.now.naaga.game.domain;

import com.now.naaga.place.domain.Position;
import org.springframework.stereotype.Component;

import static com.now.naaga.game.domain.EndType.ARRIVED;

@Component
public class GameManageService {

    public void endGame(final Game game,
                        final EndType endType,
                        final Position position) {
        if (endType == ARRIVED) {
            game.subtractAttempts();
        }
        game.endGame(position, endType);
    }
}
