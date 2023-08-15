package com.now.naaga.player.domain;

import com.now.naaga.common.domain.BaseEntity;
import com.now.naaga.member.domain.Member;
import com.now.naaga.score.domain.Score;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import java.util.Objects;

@Entity
public class Player extends BaseEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @AttributeOverride(name = "value", column = @Column(name = "total_score"))
    @Embedded
    private Score totalScore;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    private boolean deleted = false;

    protected Player() {
    }

    public Player(final String nickname,
                  final Score totalScore,
                  final Member member) {
        this(null, nickname, totalScore, member, false);
    }

    public Player(final Long id, final String nickname, final Score totalScore, final Member member, final boolean deleted) {
        this.id = id;
        this.nickname = nickname;
        this.totalScore = totalScore;
        this.member = member;
        this.deleted = deleted;
    }

    public void addScore(Score score) {
        this.totalScore = this.totalScore.plus(score);
    }

    public void delete() {
        this.deleted = true;
        this.totalScore = null;
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
