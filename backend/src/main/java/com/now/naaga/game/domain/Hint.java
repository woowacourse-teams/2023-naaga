package com.now.naaga.game.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.domain.Position;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
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
public class Hint extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Embedded
    private Position position;

    @Enumerated(EnumType.STRING)
    private Direction direction;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private Game game;

    protected Hint() {
    }

    public Hint(final Position position,
                final Direction direction,
                final Game game) {
        this(null, position, direction, game);
    }

    public Hint(final Long id,
                final Position position,
                final Direction direction,
                final Game game) {
        this.id = id;
        this.position = position;
        this.direction = direction;
        this.game = game;
    }

    public Long getId() {
        return id;
    }

    public Position getPosition() {
        return position;
    }

    public Direction getDirection() {
        return direction;
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
        final Hint hint = (Hint) o;
        return Objects.equals(id, hint.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Hint{" +
                "id=" + id +
                ", position=" + position +
                ", direction=" + direction +
                ", gameId=" + game.getId() +
                '}';
    }
}
