package com.now.naaga.letter.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Letter extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player registeredPlayer;

    @Embedded
    private Position position;

    private String message;

    public Letter() {
    }

    public Letter(final Player registeredPlayer,
                  final Position position,
                  final String message) {
        this(null, registeredPlayer, position, message);
    }

    public Letter(final Long id,
                  final Player registeredPlayer,
                  final Position position,
                  final String message) {
        this.id = id;
        this.registeredPlayer = registeredPlayer;
        this.position = position;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Player getRegisteredPlayer() {
        return registeredPlayer;
    }

    public Position getPosition() {
        return position;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Letter letter = (Letter) o;
        return Objects.equals(id, letter.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Letter{" +
                "id=" + id +
                ", playerId=" + registeredPlayer.getId() +
                ", position=" + position +
                ", message='" + message +
                '}';
    }
}
