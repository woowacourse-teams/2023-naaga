package com.now.naaga.game.presentation.dto;

import com.now.naaga.place.domain.Position;

public record CoordinateResponse(Double latitude,
                                 Double longitude) {
    public static CoordinateResponse of(final Position position) {
        return new CoordinateResponse(position.getLatitude().doubleValue(), position.getLongitude().doubleValue());
    }
}
