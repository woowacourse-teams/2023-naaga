package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.MemberFixture.*;

import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;

public class PlayerFixture {

    public static final String NICKNAME = "player_nickname";

    public static final Score SCORE = new Score(0);

    public static Player PLAYER() {
        return new Player(NICKNAME, SCORE, MEMBER());
    }
}
