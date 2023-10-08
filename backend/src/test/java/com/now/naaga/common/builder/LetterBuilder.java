package com.now.naaga.common.builder;

import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.now.naaga.common.fixture.LetterFixture.MESSAGE;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;

@Component
public class LetterBuilder {

    @Autowired
    private LetterRepository letterRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    private Optional<Player> registeredPlayer;

    private Position position;

    private String message;

    public LetterBuilder init() {
        this.registeredPlayer = Optional.empty();
        this.position = 잠실역_교보문고_좌표;
        this.message = MESSAGE;
        return this;
    }

    public LetterBuilder registeredPlayer(final Player persistedPlayer) {
        this.registeredPlayer = Optional.ofNullable(persistedPlayer);
        return this;
    }

    public LetterBuilder position(final Position position) {
        this.position = position;
        return this;
    }

    public LetterBuilder message(final String message) {
        this.message = message;
        return this;
    }

    public Letter build() {
        final Player persistedPlayer = registeredPlayer.orElseGet(this::getPersistedPlayer);
        final Letter letter = new Letter(persistedPlayer, position, message);
        return letterRepository.save(letter);
    }

    private Player getPersistedPlayer() {
        return playerBuilder.init()
                .build();
    }
}
