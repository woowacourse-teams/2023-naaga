package com.now.naaga.common.builder;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.domain.Game.MAX_ATTEMPT_COUNT;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GameBuilder {

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    private GameStatus gameStatus;

    private Optional<Player> player;

    private Optional<Place> place;

    private Position startPosition;

    private int remainingAttempts;

    private List<Hint> hints;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    public GameBuilder init() {
        this.gameStatus = IN_PROGRESS;
        this.player = Optional.empty();
        this.place = Optional.empty();
        this.startPosition = 잠실_루터회관_정문_좌표;
        this.remainingAttempts = MAX_ATTEMPT_COUNT;
        this.hints = new ArrayList<>();
        this.startTime = LocalDateTime.now();
        this.endTime = null;
        return this;
    }

    public GameBuilder gameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        return this;
    }

    public GameBuilder player(final Player player) {
        this.player = Optional.ofNullable(player);
        return this;
    }

    public GameBuilder place(final Place place) {
        this.place = Optional.ofNullable(place);
        return this;
    }

    public GameBuilder startPosition(final Position startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public GameBuilder remainingAttempts(final int remainingAttempts) {
        this.remainingAttempts = remainingAttempts;
        return this;
    }

    public GameBuilder startTime(final LocalDateTime startTime) {
        this.startTime = startTime;
        return this;
    }

    public GameBuilder endTime(final LocalDateTime endTime) {
        this.endTime = endTime;
        return this;
    }

    public Game build() {
        final Player persistedPlayer = player.orElseGet(this::getPersistedPlayer);
        final Place persistedPlace = place.orElseGet(this::getPersistedPlace);
        final Game game = new Game(gameStatus, persistedPlayer, persistedPlace, startPosition, remainingAttempts, hints, startTime, endTime);
        return gameRepository.save(game);
    }

    private Player getPersistedPlayer() {
        return playerBuilder.init()
                            .build();
    }

    private Place getPersistedPlace() {
        return placeBuilder.init()
                           .build();
    }
}
