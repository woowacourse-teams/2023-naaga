package com.now.naaga.game.domain;

import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import jakarta.persistence.*;

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

    public Game(final Member member, final Place place) {
        this(null, GameStatus.IN_PROGRESSING, member, place);
    }

    public Game(final Long id, final GameStatus gameStatus, final Member member, final Place place) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.member = member;
        this.place = place;
    }

    public void validateOwner(final Member member) {
        if (!member.equals(this.member)) {
            throw new IllegalArgumentException("접근할 수 없는 게임입니다.");
        }
    }

    public void validateInRange(final Position position) {
        if (!place.isInValidRange(position)) {
            throw new IllegalArgumentException("도착범위안에 위치하지 않습니다.");
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
