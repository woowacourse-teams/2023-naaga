package com.now.naaga.game.fixture;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.place.domain.Place;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.now.naaga.place.fixture.PlaceFixture.JEJU_PLACE;
import static com.now.naaga.place.fixture.PositionFixture.SEOUL_POSITION;
import static com.now.naaga.player.fixture.PlayerFixture.PLAYER;

public class GameFixture {

    private static final int MAXIMUM_ATTEMPTS = 5;

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

    public static Game SEOUL_TO_JEJU_GAME(final Place place) {
        return new Game(
                GameStatus.IN_PROGRESS,
                place.getRegisteredPlayer(),
                place,
                SEOUL_POSITION(),
                MAXIMUM_ATTEMPTS,
                new ArrayList<>(),
                LocalDateTime.now());
    }
}
