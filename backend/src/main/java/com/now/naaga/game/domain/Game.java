package com.now.naaga.game.domain;

import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
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
        this(null, GameStatus.IN_PROGRESS, member, place);
    }

    public Game(final Long id, final GameStatus gameStatus, final Member member, final Place place) {
        this.id = id;
        this.gameStatus = gameStatus;
        this.member = member;
        this.place = place;
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
