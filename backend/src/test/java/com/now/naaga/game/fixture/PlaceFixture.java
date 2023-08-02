package com.now.naaga.game.fixture;

import static com.now.naaga.game.fixture.PositionFixture.GS25_방이도곡점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.던킨도너츠_올림픽공원점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.역삼역_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.fixture.PositionFixture.파리바게트_방이시장점_좌표;

import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;

public class PlaceFixture {

    public static Place 잠실_루터회관(Player player) {
        return new Place("잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE",
                player);
    }

    public static Place 잠실역_교보문고(Player player) {
        return new Place("잠실역교보문고", "잠실역교보문고이다.", 잠실역_교보문고_좌표, "잠실역교보문고IMAGE",
                player);
    }

    public static Place GS25_방이도곡점(Player player) {
        return new Place("GS25방이도곡점", "GS25방이도곡점이다.", GS25_방이도곡점_좌표, "GS25방이도곡점IMAGE",
                player);
    }

    public static Place 던킨도너츠_올림픽공원점(Player player) {
        return new Place("던킨도너츠올림픽공원점", "던킨도너츠올림픽공원점이다.", 던킨도너츠_올림픽공원점_좌표, "던킨도너츠올림픽공원점IMAGE",
                player);
    }

    public static Place 역삼역(Player player) {
        return new Place("역삼역", "역삼역이다.", 역삼역_좌표, "역삼역IMAGE",
                player);
    }

    public static Place 파리바게트_방이시장점(Player player) {
        return new Place("파리바게트방이시장점", "파리바게트방이시장점이다.", 파리바게트_방이시장점_좌표, "파리바게트방이시장점IMAGE",
                player);
    }
}
