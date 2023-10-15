package com.now.naaga.letter.domain.letterlog;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.exception.LetterExceptionType;
import com.now.naaga.letter.domain.Letter;
import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class WriteLetterLog extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "letter_id")
    private Letter letter;

    protected WriteLetterLog() {
    }

    public WriteLetterLog(final Game game,
                          final Letter letter) {
        this(null, game, letter);
    }

    public WriteLetterLog(final Long id,
                          final Game game,
                          final Letter letter) {
        this.id = id;
        this.game = game;
        this.letter = letter;
    }

    public void validateOwner(final long playerId) {
        if (this.letter.getRegisteredPlayer().getId() == playerId) {
            throw new LetterException(LetterExceptionType.INACCESSIBLE_AUTHENTICATION);
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WriteLetterLog that = (WriteLetterLog) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "WriteLetterLog{" +
                "id=" + id +
                ", gameId=" + game.getId() +
                ", letterId=" + letter.getId() +
                '}';
    }
}
