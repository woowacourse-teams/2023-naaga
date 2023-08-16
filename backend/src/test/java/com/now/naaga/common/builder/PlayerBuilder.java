package com.now.naaga.common.builder;

import static com.now.naaga.common.fixture.PlayerFixture.NICKNAME;
import static com.now.naaga.common.fixture.PlayerFixture.SCORE;

import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlayerBuilder {

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private MemberBuilder memberBuilder;

    private String nickname;

    private Score totalScore;

    private Optional<Member> member;

    public PlayerBuilder init() {
        this.nickname = NICKNAME;
        this.totalScore = SCORE;
        this.member = Optional.empty();
        return this;
    }

    public PlayerBuilder nickname(final String nickname) {
        this.nickname = nickname;
        return this;
    }

    public PlayerBuilder totalScore(final Score totalScore) {
        this.totalScore = totalScore;
        return this;
    }

    public PlayerBuilder member(final Member persistedMember) {
        this.member = Optional.ofNullable(persistedMember);
        return this;
    }

    public Player build() {
        final Member persistedMember = member.orElseGet(this::getPersistedMember);
        final Player player = new Player(nickname, totalScore, persistedMember);
        return playerRepository.save(player);
    }

    private Member getPersistedMember() {
        return memberBuilder.init()
                            .build();
    }
}
