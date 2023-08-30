package com.now.naaga.game.domain;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.EndType.GIVE_UP;
import static com.now.naaga.game.domain.GameStatus.DONE;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.gameresult.domain.ResultType.FAIL;
import static com.now.naaga.gameresult.domain.ResultType.SUCCESS;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_DONE;
import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;
import static com.now.naaga.game.exception.GameExceptionType.NOT_ARRIVED;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.game.exception.GameNotArrivalException;
import com.now.naaga.gameresult.domain.ResultType;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public boolean canUseMoreHint() {
        return hints.size() < MAX_HINT_COUNT;
    }

    public void endGame(final Position position,
                        final EndType endType) {
        validateInProgressing();

        this.remainingAttempts = this.remainingAttempts - 1;
        this.endTime = LocalDateTime.now();
        
        if (isUnfinishedCondition(position, endType)) {
            throw new GameNotArrivalException(NOT_ARRIVED);
        }
    }

    private boolean isUnfinishedCondition(final Position position,
                                         final EndType endType) {
        return remainingAttempts > 0
                && !place.isCoordinateInsideBounds(position)
                && endType == GIVE_UP;
    }

    private void validateInProgressing() {
        if (gameStatus == DONE) {
            throw new GameException(ALREADY_DONE);
        }
    }
    
    public double findDistance() {
        final Position destinationPosition = place.getPosition();
        return startPosition.calculateDistance(destinationPosition);
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
