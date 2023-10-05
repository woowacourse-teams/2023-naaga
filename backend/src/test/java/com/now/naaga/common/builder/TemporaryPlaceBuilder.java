package com.now.naaga.common.builder;

import static com.now.naaga.common.fixture.PlaceFixture.DESCRIPTION;
import static com.now.naaga.common.fixture.PlaceFixture.IMAGE_URL;
import static com.now.naaga.common.fixture.PlaceFixture.NAME;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;

import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TemporaryPlaceBuilder {

    @Autowired
    private TemporaryPlaceRepository temporaryPlaceRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    private String name;

    private String description;

    private Position position;

    private String imageUrl;

    private Optional<Player> registeredPlayer;

    public TemporaryPlaceBuilder init() {
        this.name = NAME;
        this.description = DESCRIPTION;
        this.position = 잠실역_교보문고_좌표;
        this.imageUrl = IMAGE_URL;
        this.registeredPlayer = Optional.empty();
        return this;
    }

    public TemporaryPlaceBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public TemporaryPlaceBuilder description(final String description) {
        this.description = description;
        return this;
    }

    public TemporaryPlaceBuilder position(final Position position) {
        this.position = position;
        return this;
    }

    public TemporaryPlaceBuilder imageUrl(final String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public TemporaryPlaceBuilder registeredPlayer(final Player persistedPlayer) {
        this.registeredPlayer = Optional.ofNullable(persistedPlayer);
        return this;
    }

    public TemporaryPlace build() {
        final Player persistedPlayer = registeredPlayer.orElseGet(this::getPersistedPlayer);
        final TemporaryPlace temporaryPlace = new TemporaryPlace(name, description, position, imageUrl, persistedPlayer);
        return temporaryPlaceRepository.save(temporaryPlace);
    }

    private Player getPersistedPlayer() {
        return playerBuilder.init()
                            .build();
    }
}
