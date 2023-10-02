package com.now.naaga.temporaryplace.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class TemporaryPlace extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    private String name;

    private String description;

    @Embedded
    private Position position;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player registeredPlayer;

    protected TemporaryPlace() {
    }

    public TemporaryPlace(final String name,
                          final String description,
                          final Position position,
                          final String imageUrl,
                          final Player registeredPlayer) {
        this(null, name, description, position, imageUrl, registeredPlayer);
    }

    public TemporaryPlace(final Long id,
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
        final TemporaryPlace that = (TemporaryPlace) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TemporaryPlace{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", position=" + position +
                ", imageUrl='" + imageUrl + '\'' +
                ", registeredPlayerId=" + registeredPlayer.getId() +
                '}';
    }
}
