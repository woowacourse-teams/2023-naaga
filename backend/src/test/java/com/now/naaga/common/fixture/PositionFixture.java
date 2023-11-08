package com.now.naaga.common.fixture;

import com.now.naaga.place.domain.Position;
import java.math.BigDecimal;

public class PositionFixture {

    public static final Position 잠실_루터회관_정문_좌표 = new Position(BigDecimal.valueOf(37.515446),
                                                              BigDecimal.valueOf(127.102899));

    public static final Position 잠실_루터회관_정문_근처_좌표 = new Position(BigDecimal.valueOf(37.515546),
                                                                 BigDecimal.valueOf(127.102902));

    public static final Position 잠실역_교보문고_좌표 = new Position(BigDecimal.valueOf(37.514258),
                                                            BigDecimal.valueOf(127.100883));

    public static final Position 잠실역_교보문고_110미터_앞_좌표 = new Position(잠실역_교보문고_좌표.getLatitude().add(BigDecimal.valueOf(0.000991)),
            잠실역_교보문고_좌표.getLongitude());

    public static final Position GS25_방이도곡점_좌표 = new Position(BigDecimal.valueOf(37.512184),
                                                              BigDecimal.valueOf(127.112789));


    public static final Position 던킨도너츠_올림픽공원점_좌표 = new Position(BigDecimal.valueOf(37.5169677),
                                                                BigDecimal.valueOf(127.11207));

    public static final Position 장미_상가 = new Position(BigDecimal.valueOf(37.517109),
                                                    BigDecimal.valueOf(127.100466));

    //잠실 루터회관 1km 밖 =>

    public static final Position 역삼역_좌표 = new Position(BigDecimal.valueOf(37.500845),
                                                       BigDecimal.valueOf(127.036953));

    public static final Position 파리바게트_방이시장점_좌표 = new Position(BigDecimal.valueOf(37.511737),
                                                               BigDecimal.valueOf(127.114016));

    public static final Position 서울_좌표 = new Position(new BigDecimal("37.535978"),
                                                      new BigDecimal("126.981654"));

    public static final Position 제주_좌표 = new Position(new BigDecimal("33.384929"),
                                                      new BigDecimal("126.529675"));
}
