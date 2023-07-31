package com.now.naaga.game.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.place.domain.Position;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class DirectionTest {

    @ParameterizedTest(name = "위경도 ({0}, {1}) 기준으로, 위경도 ({2}, {3})은 {4} 방향이다.")
    @CsvSource(value = {
            "34.460335, 126.047293, 35.829468, 126.750417, NORTH",
            "34.460335, 126.047293, 38.200656, 125.220855, NORTH",
            "34.460335, 126.047293, 35.367904, 127.824117, EAST",
            "34.460335, 126.047293, 34.631412, 128.562752, EAST",
            "34.460335, 126.047293, 32.992399, 125.611446, SOUTH",
            "34.460335, 126.047293, 33.850022, 126.148157, SOUTH",
            "34.460335, 126.047293, 34.800887, 123.969202, WEST",
            "34.460335, 126.047293, 35.465752, 123.155567, WEST",
    })
    void 위도와_경도를_통해_방향을_결정한다(final BigDecimal fromLatitude,
                             final BigDecimal fromLongitude,
                             final BigDecimal toLatitude,
                             final BigDecimal toLongitude,
                             final String expected) {
        // given
        final Position from = new Position(fromLatitude, fromLongitude);
        final Position to = new Position(toLatitude, toLongitude);

        // when
        final Direction actual = Direction.calculate(from, to);

        // then
        assertThat(actual).isEqualTo(Direction.valueOf(expected));
    }
}