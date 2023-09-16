package com.now.naaga.common.builder;

import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component

public class PlaceLikeBuilder {

    @Autowired
    private PlaceLikeRepository placeLikeRepository;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    private PlaceLikeType placeLikeType;

    private Optional<Place> place;

    private Optional<Player> player;

    public PlaceLikeBuilder init() {
        this.placeLikeType = PlaceLikeType.LIKE;
        this.place = Optional.empty();
        this.player = Optional.empty();
        return this;
    }

    public PlaceLikeBuilder placeLikeType(final PlaceLikeType placeLikeType) {
        this.placeLikeType = placeLikeType;
        return this;
    }

    public PlaceLikeBuilder place(final Place persistedPlace) {
        this.place = Optional.ofNullable(persistedPlace);
        return this;
    }

    public PlaceLikeBuilder player(final Player persistedPlayer) {
        this.player = Optional.ofNullable(persistedPlayer);
        return this;
    }

    public PlaceLike build() {
        final Place persistePlace = place.orElseGet(this::getPersistedPlace);
        final Player persistedPlayer = player.orElseGet(this::getPersistedPlayer);
        final PlaceLike placeLike = new PlaceLike(persistePlace, persistedPlayer, placeLikeType);
        return placeLikeRepository.save(placeLike);
    }

    private Player getPersistedPlayer() {
        return playerBuilder.init()
                .build();
    }

    private Place getPersistedPlace() {
        return placeBuilder.init()
                .build();
    }
}
