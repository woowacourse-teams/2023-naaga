package com.now.naaga.place.presentation.dto;

import com.now.naaga.place.domain.Position;
import java.math.RoundingMode;

public record CoordinateResponse(Double latitude,
                                 Double longitude) {

    public static CoordinateResponse from(final Position position) {
        return new CoordinateResponse(
                position.getLatitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue(),
                position.getLongitude().setScale(6, RoundingMode.HALF_DOWN).doubleValue()
        );
    }
}
