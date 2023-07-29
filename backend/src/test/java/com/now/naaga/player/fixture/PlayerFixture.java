package com.now.naaga.player.fixture;

import com.now.naaga.member.fixture.MemberFixture;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import com.now.naaga.score.fixture.ScoreFixture;

public class PlayerFixture {

    public static final Player PLAYER = new Player("krrong", ScoreFixture.SCORE, MemberFixture.MEMBER);

}
