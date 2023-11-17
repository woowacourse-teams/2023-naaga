package com.now.naaga.player.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.exception.PlayerExceptionType;
import com.now.naaga.score.domain.Score;
import jakarta.persistence.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.util.Objects;
import java.util.regex.Pattern;

import static java.lang.Boolean.FALSE;

@SQLDelete(sql = "UPDATE player SET deleted = true WHERE id = ?")
@Where(clause = "deleted = false")
@Entity
public class Player extends BaseEntity {

    private static final int NICKNAME_MAX_SIZE = 20;

    private static final int NICKNAME_MIN_SIZE = 2;

    private static final Pattern NICKNAME_PATTERN = Pattern.compile("^.*[^가-힣a-zA-Z0-9\\s]+.*$");

    private static final String UNAVAILABLE_REGEX = "[^가-힣a-zA-Z0-9\\s]+";

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @AttributeOverride(name = "value", column = @Column(name = "total_score"))
    @Embedded
    private Score totalScore;

    @JoinColumn(name = "member_id")
    @OneToOne(fetch = FetchType.LAZY)
    private Member member;

    private boolean deleted = FALSE;

    protected Player() {
    }

    public Player(final String nickname,
                  final Score totalScore,
                  final Member member) {
        this(null, nickname, totalScore, member, FALSE);
    }

    public Player(final Long id,
                  final String nickname,
                  final Score totalScore,
                  final Member member,
                  final boolean deleted) {
        validateNickname(nickname);
        this.id = id;
        this.nickname = nickname;
        this.totalScore = totalScore;
        this.member = member;
        this.deleted = deleted;
    }

    public static Player create(final String nickname,
                                final Score score,
                                final Member member) {
        final String modifiedNickname = modifyToValidNickname(nickname);
        return new Player(modifiedNickname, score, member);
    }

    private static String modifyToValidNickname(final String nickname) {
        final String modifiedNickname = nickname.replaceAll(UNAVAILABLE_REGEX, "");
        if (modifiedNickname.length() > 20) {
            return modifiedNickname.substring(0, NICKNAME_MAX_SIZE);
        }
        return modifiedNickname;
    }

    public void editNickname(final String newNickname) {
        validateNickname(newNickname);
        this.nickname = newNickname;
    }

    private void validateNickname(final String nickname) {
        final boolean isUnavailableNickname = NICKNAME_PATTERN.matcher(nickname).matches();
        if(isUnavailableNickname || nickname.length() < NICKNAME_MIN_SIZE || nickname.length() > NICKNAME_MAX_SIZE) {
            throw new PlayerException(PlayerExceptionType.UNAVAILABLE_NICKNAME);
        }
    }

    public void addScore(Score score) {
        this.totalScore = this.totalScore.plus(score);
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public Score getTotalScore() {
        return totalScore;
    }

    public Member getMember() {
        return member;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Player player = (Player) o;
        return Objects.equals(id, player.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Player{" +
                "id=" + id +
                ", nickname='" + nickname + '\'' +
                ", totalScore=" + totalScore +
                ", memberId=" + member.getId() +
                '}';
    }
}
