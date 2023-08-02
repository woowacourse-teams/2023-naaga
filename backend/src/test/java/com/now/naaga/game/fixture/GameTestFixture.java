package com.now.naaga.game.fixture;

import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import java.math.BigDecimal;

public class GameTestFixture {
    
    
    public static final Member MEMBER1 = new Member("irye@gmail.com", "0104");
    public static final Member MEMBER2 = new Member("chae@gmail.com", "0121");
    public static final Player REGISTER = new Player(1l,"irye", new Score(1000), MEMBER1);
    
    
    public static final Position 잠실_루터회관_정문_좌표 = new Position(BigDecimal.valueOf(37.515446),
            BigDecimal.valueOf(127.102899));
    public static final Position 잠실_루터회관_정문_근처_좌표 = new Position(BigDecimal.valueOf(37.515546),
            BigDecimal.valueOf(127.102902));
    public static final Position 잠실역_교보문고_좌표 = new Position(BigDecimal.valueOf(37.514258),
            BigDecimal.valueOf(127.100883));
    public static final Position GS25_방이도곡점_좌표 = new Position(BigDecimal.valueOf(37.512184),
            BigDecimal.valueOf(127.112789));
    public static final Position 던킨도너츠_올림픽공원점_좌표 = new Position(BigDecimal.valueOf(37.5169677),
            BigDecimal.valueOf(127.11207));
    //잠실 루터회관 1km 밖 =>
    public static final Position 역삼역_좌표 = new Position(BigDecimal.valueOf(37.500845),
            BigDecimal.valueOf(127.036953));
    public static final Position 파리바게트_방이시장점_좌표 = new Position(BigDecimal.valueOf(37.511737),
            BigDecimal.valueOf(127.114016));
    
    
    public static final Place 잠실_루터회관 = new Place(1l, "잠실루터회관", "잠실루터회관이다.", 잠실_루터회관_정문_좌표, "잠실루터회관IMAGE",
            REGISTER);
    public static final Place 잠실역_교보문고 = new Place(2l, "잠실역교보문고", "잠실역교보문고이다.", 잠실역_교보문고_좌표, "잠실역교보문고IMAGE",
            REGISTER);
    public static final Place GS25_방이도곡점 = new Place(3l, "GS25방이도곡점", "GS25방이도곡점이다.", GS25_방이도곡점_좌표, "GS25방이도곡점IMAGE",
            REGISTER);
    public static final Place 던킨도너츠_올림픽공원점 = new Place(4l, "던킨도너츠올림픽공원점", "던킨도너츠올림픽공원점이다.", 던킨도너츠_올림픽공원점_좌표, "던킨도너츠올림픽공원점IMAGE",
            REGISTER);
    public static final Place 역삼역 = new Place(5l, "역삼역", "역삼역이다.", 역삼역_좌표, "역삼역IMAGE",
            REGISTER);
    public static final Place 파리바게트_방이시장점 = new Place(6l, "파리바게트방이시장점", "파리바게트방이시장점이다.", 파리바게트_방이시장점_좌표, "파리바게트방이시장점IMAGE",
            REGISTER);
}
