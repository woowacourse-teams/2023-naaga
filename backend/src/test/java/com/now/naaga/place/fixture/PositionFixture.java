package com.now.naaga.place.fixture;

import com.now.naaga.place.domain.Position;

import java.math.BigDecimal;

public class PositionFixture {

    public static Position SEOUL_POSITION() {
        return new Position(
                new BigDecimal("37.535978"),
                new BigDecimal("126.981654"));
    }

    public static Position JEJU_POSITION() {
        return new Position(
                new BigDecimal("33.384929"),
                new BigDecimal("126.529675"));
    }
}
