package com.now.naaga.common.builder;

import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ReadLetterLogBuilder {

    @Autowired
    private ReadLetterLogRepository readLetterLogRepository;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private LetterBuilder letterBuilder;

    private Optional<Game> registeredGame;

    private Optional<Letter> registeredLetter;

    public ReadLetterLogBuilder init() {
        this.registeredGame = Optional.empty();
        this.registeredLetter = Optional.empty();
        return this;
    }

    public ReadLetterLogBuilder game(final Game registeredGame) {
        this.registeredGame = Optional.ofNullable(registeredGame);
        return this;
    }

    public ReadLetterLogBuilder letter(final Letter registerdLetter) {
        this.registeredLetter = Optional.ofNullable(registerdLetter);
        return this;
    }

    public ReadLetterLog build() {
        final Game persistedgame = registeredGame.orElseGet(this::getPersitedGame);
        final Letter persistedLetter = registeredLetter.orElseGet(this::getPersistedLetter);
        final ReadLetterLog letterLog = new ReadLetterLog(persistedgame, persistedLetter);
        return readLetterLogRepository.save(letterLog);
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
