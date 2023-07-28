package com.now.naaga.game.domain;

import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;
import static com.now.naaga.game.exception.GameExceptionType.NOT_ARRIVED;

import com.now.naaga.game.exception.GameException;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Game {

    public static final double MIN_RANGE = 0.05;

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Enumerated(EnumType.STRING)
    private GameStatus gameStatus;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    protected Game() {
    }

    public Game(final Member member,
                final Place place) {
        this(null, GameStatus.IN_PROGRESS, member, place);
    }

    public Game(final Long id,
                final GameStatus gameStatus,
                final Member member,
                final Place place) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.member = member;
        this.place = place;
    }

    public void validateOwner(final Member member) {
        if (!member.equals(this.member)) {
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

    public Long getId() {
        return id;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public Member getMember() {
        return member;
    }

    public Place getPlace() {
        return place;
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
                ", memberId=" + member.getId() +
                ", placeId=" + place.getId() +
                '}';
    }
}
