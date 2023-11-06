package com.now.naaga.common.fixture;

import static com.now.naaga.common.fixture.PlaceFixture.PLACE;
import static com.now.naaga.common.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.common.fixture.PositionFixture.서울_좌표;

import com.now.naaga.game.domain.Game;

public class GameFixture {

    public static Game GAME() {
        return new Game(PLAYER(), PLACE(), 서울_좌표);
    }
}
