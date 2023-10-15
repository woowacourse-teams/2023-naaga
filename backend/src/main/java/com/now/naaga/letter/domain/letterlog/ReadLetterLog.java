package com.now.naaga.letter.domain.letterlog;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.domain.Letter;
import jakarta.persistence.*;

import java.util.Objects;

import static com.now.naaga.letter.exception.LetterExceptionType.INACCESSIBLE_AUTHENTICATION;


@Entity
public class ReadLetterLog extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;

    protected ReadLetterLog() {
    }

    public ReadLetterLog(final Game game,
                         final Letter letter) {
        this(null, game, letter);
    }

    public ReadLetterLog(final Long id,
                         final Game game,
                         final Letter letter) {
        this.id = id;
        this.game = game;
        this.letter = letter;
    }

    public void validateOwner(final long playerId) {
        if (this.letter.getRegisteredPlayer().getId() == playerId) {
            throw new LetterException(INACCESSIBLE_AUTHENTICATION);
        }
    }

    public Long getId() {
        return id;
    }

    public Game getGame() {
        return game;
    }

    public Letter getLetter() {
        return letter;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ReadLetterLog letterLog = (ReadLetterLog) o;
        return Objects.equals(id, letterLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "LetterLog{" +
                "id=" + id +
                ", gameId=" + game.getId() +
                ", letterId=" + letter.getId() +
                '}';
    }
}
