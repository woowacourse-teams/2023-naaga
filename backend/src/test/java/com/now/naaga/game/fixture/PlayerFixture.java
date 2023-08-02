package com.now.naaga.game.fixture;

import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;

public class PlayerFixture {
    
    public static Player PLAYER(String nickname, Member member) {
        return new Player(nickname, new Score(1000), member);
    }
    
}
