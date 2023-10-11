package com.now.naaga.common.builder;

import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letterlog.domain.LetterLog;
import com.now.naaga.letterlog.domain.LetterLogType;
import com.now.naaga.letterlog.repository.LetterLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class LetterLogBuilder {

    @Autowired
    private LetterLogRepository letterLogRepository;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    private Optional<Game> registeredGame;

    private Optional<Letter> registeredLetter;

    private LetterLogType letterLogType;

    public LetterLogBuilder init() {
        this.registeredGame = Optional.empty();
        this.registeredLetter = Optional.empty();
        return this;
    }

    public LetterLogBuilder game(final Game registeredGame) {
        this.registeredGame = Optional.ofNullable(registeredGame);
        return this;
    }

    public LetterLogBuilder letter(final Letter registerdLetter) {
        this.registeredLetter = Optional.ofNullable(registerdLetter);
        return this;
    }

    public LetterLogBuilder letterLogType(final LetterLogType letterLogType) {
        this.letterLogType = letterLogType;
        return this;
    }

    public LetterLog build() {
        final Game persistedgame = registeredGame.orElseGet(this::getPersitedGame);
        final Letter persistedLetter = registeredLetter.orElseGet(this::getPersistedLetter);
        final LetterLog letterLog = new LetterLog(persistedgame, persistedLetter, letterLogType);
        return letterLogRepository.save(letterLog);
    }

    public Game getPersitedGame() {
        return gameBuilder.init()
                .build();
    }

    public Letter getPersistedLetter() {
        return letterBuilder.init()
                .build();
    }
}
