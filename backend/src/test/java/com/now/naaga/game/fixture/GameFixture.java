package com.now.naaga.game.fixture;

import static com.now.naaga.game.domain.Game.MAXIMUM_ATTEMPTS;
import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class GameFixture {

    public static Game SEOUL_TO_JEJU_GAME() {
        return new Game(
                GameStatus.IN_PROGRESS,
                PLAYER(),
                JEJU_PLACE(),
                SEOUL_POSITION(),
                MAXIMUM_ATTEMPTS,
                new ArrayList<>(),
                LocalDateTime.now());
    }
}
