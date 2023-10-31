package com.now.naaga.place.domain;

import static com.now.naaga.game.domain.Game.MIN_RANGE;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.player.domain.Player;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class Place extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private String description;

    @Embedded
    private Position position;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player registeredPlayer;

    protected Place() {
    }

    public Place(final String name,
                 final String description,
                 final Position position,
                 final String imageUrl,
                 final Player registeredPlayer) {
        this(null, name, description, position, imageUrl, registeredPlayer);
    }

    public Place(final Long id,
                 final String name,
                 final String description,
                 final Position position,
                 final String imageUrl,
                 final Player registeredPlayer) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.position = position;
        this.imageUrl = imageUrl;
        this.registeredPlayer = registeredPlayer;
    }

    public boolean isCoordinateInsideBounds(final Position other) {
        return position.calculateDistance(other) <= MIN_RANGE;
    }

    public void validateOwner(final Player player) {
        if (!this.registeredPlayer.equals(player)) {
            throw new PlaceException(PlaceExceptionType.INACCESSIBLE_AUTHENTICATION);
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Position getPosition() {
        return position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Player getRegisteredPlayer() {
        return registeredPlayer;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Place place = (Place) o;
        return Objects.equals(id, place.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Place{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", imageUrl='" + imageUrl + '\'' +
                ", registeredPlayer=" + registeredPlayer +
                '}';
    }
}
