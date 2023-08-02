package com.now.naaga.game.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.exception.GameException;
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
import static com.now.naaga.game.domain.ResultType.FAIL;
import static com.now.naaga.game.domain.ResultType.SUCCESS;
import static com.now.naaga.game.exception.GameExceptionType.ALREADY_DONE;
import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;
import static com.now.naaga.game.exception.GameExceptionType.NOT_ARRIVED;

@Entity
public class Game extends BaseEntity {
    
    // todo : 건물 내부로 좌표를 찍으면 20미터로 어림도 없다.. 30미터 괜찮을지도?
    public static final int MAX_ATTEMPT_COUNT = 5;
    public static final int MAX_HINT_COUNT = 3;
    public static final double MIN_RANGE = 0.05;

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

    public Game(final Player player, final Place place, final Position startPosition) {
        this(null, GameStatus.IN_PROGRESS, player, place, startPosition, 5, new ArrayList<>(), LocalDateTime.now(), null);
    }

    public Game(final GameStatus gameStatus,
                final Player player,
                final Place place,
                final Position startPosition,
                final int remainingAttempts,
                final List<Hint> hints,
                final LocalDateTime startTime,
                final LocalDateTime endTime) {
        this(null, gameStatus, player, place, startPosition, remainingAttempts, hints, startTime,endTime);
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

    public void validatePlayer(final Player player) {
        if (!player.equals(this.player)) {
            throw new GameException(INACCESSIBLE_AUTHENTICATION);
        }
    }

    public void validateInRange(final Position position) {
        if (!place.isInValidRange(position)) {
            throw new GameException(NOT_ARRIVED);
        }
    }

    public void changeGameStatus(final GameStatus gameStatus) {
        this.gameStatus = gameStatus;
    }
    
    public ResultType endGame(EndType endType, Position position) {
        if (isDone()) {
            throw new GameException(ALREADY_DONE);
        }
        if (endType == GIVE_UP) {
            return giveUpGame();
        }
        return endGameByArrival(position);
    }
    
    private boolean isDone() {
        return gameStatus == DONE;
    }
    
    private ResultType giveUpGame() {
        gameStatus = DONE;
        endTime = LocalDateTime.now();
        return FAIL;
    }
    
    private ResultType endGameByArrival(Position position) {
        remainingAttempts--;
        if (isPlayerArrived(position)) {
            return endGameWithSuccess();
        }
        return endGameWithFailure();
    }
    
    // todo : place의 변수명을 isCoordinateInsideBounds 또는 isPositionInsideBounds 또는 isPositionWithinRange로 바꾸고 싶다
    //또, 이 메서드에 범위를 함께 넘겨주는 것이 마땅해보인다.
    private boolean isPlayerArrived(Position position) {
        return place.isInValidRange(position);
    }
    
    private ResultType endGameWithSuccess() {
        gameStatus = DONE;
        endTime = LocalDateTime.now();
        return SUCCESS;
    }
    
    private ResultType endGameWithFailure() {
        if (remainingAttempts == 0) {
            gameStatus = DONE;
            endTime = LocalDateTime.now();
            return FAIL;
        }
        throw new GameException(NOT_ARRIVED);
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
