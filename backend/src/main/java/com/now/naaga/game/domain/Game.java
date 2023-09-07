package com.now.naaga.game.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameNotFinishedException;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.now.naaga.game.domain.EndType.GIVE_UP;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.*;

@Entity
public class Game extends BaseEntity {

    public static final double MIN_RANGE = 50;
    public static final int MAX_HINT_COUNT = 5;
    public static final int MAX_ATTEMPT_COUNT = 3;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @AttributeOverrides({
            @AttributeOverride(name = "latitude", column = @Column(name = "start_latitude", precision = 9, scale = 6)),
            @AttributeOverride(name = "longitude", column = @Column(name = "start_longitude", precision = 9, scale = 6))
    })
    @Embedded
    private Position startPosition;

    private int remainingAttempts;

    @OneToMany(mappedBy = "game")
    private List<Hint> hints;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    protected Game() {
    }

    public Game(final Player player,
                final Place place,
                final Position startPosition) {
        this(null, IN_PROGRESS, player, place, startPosition, MAX_ATTEMPT_COUNT, new ArrayList<>(), LocalDateTime.now(), null);
    }

    public Game(final GameStatus gameStatus,
                final Player player,
                final Place place,
                final Position startPosition,
                final int remainingAttempts,
                final List<Hint> hints,
                final LocalDateTime startTime,
                final LocalDateTime endTime) {
        this(null, gameStatus, player, place, startPosition, remainingAttempts, hints, startTime, endTime);
    }

    public Game(final Long id,
                final GameStatus gameStatus,
                final Player player,
                final Place place,
                final Position startPosition,
                final int remainingAttempts,
                final List<Hint> hints,
                final LocalDateTime startTime,
                final LocalDateTime endTime) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.player = player;
        this.place = place;
        this.startPosition = startPosition;
        this.remainingAttempts = remainingAttempts;
        this.hints = hints;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public void validateOwner(final Player player) {
        if (!this.player.equals(player)) {
            throw new GameException(INACCESSIBLE_AUTHENTICATION);
        }
    }

    public void validateDone() {
        if (gameStatus == IN_PROGRESS) {
            throw new GameException(NOT_DONE);
        }
    }

    public boolean canUseMoreHint() {
        return hints.size() < MAX_HINT_COUNT;
    }

    public double findDistance() {
        final Position destinationPosition = place.getPosition();
        return startPosition.calculateDistance(destinationPosition);
    }

    public void subtractAttempts() {
        remainingAttempts--;
    }

    public void endGame(final Position position,
                        final EndType endType) {
        validateInProgressing();
        validateFinishedCondition(position, endType);

        this.endTime = LocalDateTime.now();
        this.gameStatus = DONE;
    }

    private void validateFinishedCondition(final Position position,
                                           final EndType endType) {
        final boolean isUnfinishedCondition = remainingAttempts > 0
                && !place.isCoordinateInsideBounds(position)
                && endType != GIVE_UP;

        if (isUnfinishedCondition) {
            throw new GameNotFinishedException(NOT_ARRIVED);
        }
    }

    private void validateInProgressing() {
        if (gameStatus == DONE || remainingAttempts <= 0) {
            throw new GameException(ALREADY_DONE);
        }
    }

    public Long getId() {
        return id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Player getPlayer() {
        return player;
    }

    public Place getPlace() {
        return place;
    }

    public Position getStartPosition() {
        return startPosition;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public List<Hint> getHints() {
        return hints;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Game game = (Game) o;
        return Objects.equals(id, game.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Game{" +
                "id=" + id +
                ", gameStatus=" + gameStatus +
                ", playerId=" + player.getId() +
                ", placeId=" + place.getId() +
                ", startPosition=" + startPosition +
                ", remainingAttempts=" + remainingAttempts +
                ", hints=" + hints +
                ", startTime=" + startTime +
                '}';
    }
}
