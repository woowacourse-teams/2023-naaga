package com.now.naaga.place.domain.like;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
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
public class Like extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "place_id")
    private Place place;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;

    @Enumerated(EnumType.STRING)
    private Type type;

    protected Like() {
    }

    public Like(final Place place,
                final Player player,
                final Type type) {
        this(null, place, player, type);
    }

    public Like(final Long id,
                final Place place,
                final Player player,
                final Type type) {
        this.id = id;
        this.place = place;
        this.player = player;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Place getPlace() {
        return place;
    }

    public Player getPlayer() {
        return player;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Like{" +
                "id=" + id +
                ", placeId=" + place.getId() +
                ", playerId=" + player.getId() +
                ", type=" + type +
                '}';
    }
}
