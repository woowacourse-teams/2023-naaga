package com.now.naaga.game.fixture;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.place.domain.Place;

import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
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
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );
    }

    public static Game SEOUL_TO_JEJU_GAME(final Place place) {
        return new Game(
                GameStatus.IN_PROGRESS,
                place.getRegisteredPlayer(),
                place,
                SEOUL_POSITION(),
                MAXIMUM_ATTEMPTS,
                new ArrayList<>(),
                LocalDateTime.now().minusHours(1),
                LocalDateTime.now()
        );
    }
    
    public static Game GAME_IN_PROGRESS(Player player, Place place, Position startPosition) {
        return new Game(GameStatus.IN_PROGRESS, player, place, startPosition, Game.MAX_HINT_COUNT,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }

    public static Game GAME_IN_PROGRESS(Long id, Player player, Place place, Position startPosition) {
        return new Game(id, GameStatus.IN_PROGRESS, player, place, startPosition, Game.MAX_HINT_COUNT,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }

    public static Game GAME_IN_PROGRESS_WITH_REMAINING_ATTEMPTS(Player player, Place place, Position startPosition, int attempts) {
        return new Game(null, GameStatus.IN_PROGRESS, player, place, startPosition, attempts,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }
    
    public static Game GAME_IN_PROGRESS_WITH_LAST_ATTEMPTS(Player player, Place place, Position startPosition) {
        return new Game(GameStatus.IN_PROGRESS, player, place, startPosition, 1,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }
    
    public static Game GAME_IS_DONE(Player player, Place place, Position startPosition) {
        return new Game(GameStatus.DONE, player, place, startPosition, Game.MAX_HINT_COUNT,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }
    
    public static Game GAME_IS_DONE_BY_ZERO_ATTEMPS(Player player, Place place, Position startPosition) {
        return new Game(GameStatus.DONE, player, place, startPosition, 0,
                Collections.emptyList(),
                LocalDateTime.now(), null);
    }
}
