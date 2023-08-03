package com.now.naaga.game.domain;

import com.now.naaga.place.domain.Position;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

public enum Direction {

    NORTH,
    SOUTH,
    WEST,
    EAST;

    public static Direction calculate(final Position from, final Position to) {
        final BigDecimal deltaLatitude = getDeltaLatitude(from, to);
        final BigDecimal deltaLongitude = getDeltaLongitude(from, to);

        double degree = getDegree(deltaLatitude, deltaLongitude);

        if (degree > 45.0 && degree <= 135.0) {
            return EAST;
        }
        if (degree > 135.0 && degree <= 225.0) {
            return SOUTH;
        }
        if (degree > 225 && degree <= 315) {
            return WEST;
        }
        return NORTH;
    }

    private static BigDecimal getDeltaLatitude(final Position from, final Position to) {
        final BigDecimal fromLatitude = from.getLatitude();
        final BigDecimal toLatitude = to.getLatitude();
        return toLatitude.subtract(fromLatitude);
    }

    private static BigDecimal getDeltaLongitude(final Position from, final Position to) {
        final BigDecimal fromLongitude = from.getLongitude();
        final BigDecimal toLongitude = to.getLongitude();
        return toLongitude.subtract(fromLongitude);
    }

    private static double getDegree(final BigDecimal deltaLatitude, final BigDecimal deltaLongitude) {
        final BigDecimal powDeltaLatitude = deltaLatitude.pow(2);
        final BigDecimal powDeltaLongitude = deltaLongitude.pow(2);
        final BigDecimal magnitude = powDeltaLatitude.add(powDeltaLongitude).sqrt(MathContext.DECIMAL64);
        final BigDecimal val = deltaLatitude.divide(magnitude, RoundingMode.DOWN);

        double degree = Math.toDegrees(Math.acos(val.doubleValue()));

        if (deltaLongitude.doubleValue() < 0.0) {
            return 360.0 - degree;
        }

        return degree;
    }
}
