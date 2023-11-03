package com.now.naaga.letter.domain.letterlog;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
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
