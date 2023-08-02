package com.now.naaga.player.fixture;

import com.now.naaga.member.fixture.MemberFixture;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;

import static com.now.naaga.member.fixture.MemberFixture.MEMBER;

public class PlayerFixture {

    public static Player PLAYER() {
        return new Player(
                "kokodak",
                new Score(100),
                MEMBER());
    }
}
