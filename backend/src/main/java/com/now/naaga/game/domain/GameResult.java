package com.now.naaga.game.domain;

import com.now.naaga.common.domain.BaseEntity;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class GameResult extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private ResultType resultType;

    private LocalDateTime arrivedTime;

    @Embedded
    private Score score;

    @OneToOne
    @JoinColumn(name = "game_id")
    private Game game;

    protected GameResult() {
    }

    public GameResult(final ResultType resultType,
                      final LocalDateTime arrivedTime,
                      final Score score,
                      final Game game) {
        this(null, resultType, arrivedTime, score, game);
    }

    public GameResult(final Long id,
                      final ResultType resultType,
                      final LocalDateTime arrivedTime,
                      final Score score,
                      final Game game) {
        this.id = id;
        this.resultType = resultType;
        this.arrivedTime = arrivedTime;
        this.score = score;
        this.game = game;
    }

    public Long getId() {
        return id;
    }

    public ResultType getResultType() {
        return resultType;
    }

    public LocalDateTime getArrivedTime() {
        return arrivedTime;
    }

    public Score getScore() {
        return score;
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameResult that = (GameResult) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "GameResult{" +
                "id=" + id +
                ", resultType=" + resultType +
                ", arrivedTime=" + arrivedTime +
                ", score=" + score +
                ", gameId=" + game.getId() +
                '}';
    }
}
