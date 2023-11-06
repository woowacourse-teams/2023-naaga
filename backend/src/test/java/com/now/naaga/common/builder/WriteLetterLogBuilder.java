package com.now.naaga.common.builder;

import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class WriteLetterLogBuilder {

    @Autowired
    GameBuilder gameBuilder;

    @Autowired
    LetterBuilder letterBuilder;

    @Autowired
    WriteLetterLogRepository writeLetterLogRepository;

    private Optional<Game> game;

    private Optional<Letter> letter;

    public WriteLetterLogBuilder init() {
        this.game = Optional.empty();
        this.letter = Optional.empty();
        return this;
    }

    public WriteLetterLogBuilder game(Game registerdGame) {
        this.game = Optional.ofNullable(registerdGame);
        return this;
    }

    public WriteLetterLogBuilder letter(Letter registerdLetter) {
        this.letter = Optional.ofNullable(registerdLetter);
        return this;
    }

    public WriteLetterLog build() {
        final Game persistedGame = game.orElseGet(this::getPersitedGame);
        final Letter persistedLetter = letter.orElseGet(this::getPersistedLetter);
        final WriteLetterLog writeLetterLog = new WriteLetterLog(persistedGame, persistedLetter);
        return writeLetterLogRepository.save(writeLetterLog);
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
