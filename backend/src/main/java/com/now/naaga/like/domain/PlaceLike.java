package com.now.naaga.like.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.exception.GameException;
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

import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;

@Entity
public class PlaceLike extends BaseEntity {

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
    private PlaceLikeType placeLikeType;

    protected PlaceLike() {
    }

    public PlaceLike(final Place place,
                     final Player player,
                     final PlaceLikeType placeLikeType) {
        this(null, place, player, placeLikeType);
    }

    public PlaceLike(final Long id,
                     final Place place,
                     final Player player,
                     final PlaceLikeType placeLikeType) {
        this.id = id;
        this.place = place;
        this.player = player;
        this.placeLikeType = placeLikeType;
    }

    public void validateOwner(final Player player) {
        if (!this.player.equals(player)) {
            throw new GameException(INACCESSIBLE_AUTHENTICATION);
        }
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

    public PlaceLikeType getType() {
        return placeLikeType;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final PlaceLike placeLike = (PlaceLike) o;
        return Objects.equals(id, placeLike.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "PlaceLike{" +
                "id=" + id +
                ", placeId=" + place.getId() +
                ", playerId=" + player.getId() +
                ", placeLikeType=" + placeLikeType +
                '}';
    }
}
