package com.now.naaga.common.builder;

import static com.now.naaga.common.fixture.PlaceFixture.DESCRIPTION;
import static com.now.naaga.common.fixture.PlaceFixture.IMAGE_URL;
import static com.now.naaga.common.fixture.PlaceFixture.NAME;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;

import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.repository.PlaceRepository;
import com.now.naaga.player.domain.Player;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceBuilder {

    @Autowired
    private PlaceRepository placeRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    private String name;

    private String description;

    private Position position;

    private String imageUrl;

    private Optional<Player> registeredPlayer;

    public PlaceBuilder init() {
        this.name = NAME;
        this.description = DESCRIPTION;
        this.position = 잠실역_교보문고_좌표;
        this.imageUrl = IMAGE_URL;
        this.registeredPlayer = Optional.empty();
        return this;
    }

    public PlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public PlaceBuilder description(final String description) {
        this.description = description;
        return this;
    }

    public PlaceBuilder position(final Position position) {
        this.position = position;
        return this;
    }

    public PlaceBuilder imageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public PlaceBuilder registeredPlayer(final Player persistedPlayer) {
        this.registeredPlayer = Optional.ofNullable(persistedPlayer);
        return this;
    }

    public Place build() {
        final Player persistedPlayer = registeredPlayer.orElseGet(this::getPersistedPlayer);
        final Place place = new Place(name, description, position, imageUrl, persistedPlayer);
        return placeRepository.save(place);
    }

    private Player getPersistedPlayer() {
        return playerBuilder.init()
                            .build();
    }
}
