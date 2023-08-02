package com.now.naaga.game.presentation.dto;

import com.now.naaga.place.domain.Position;

//TODO:CoordinateResponse와 CoordinateRequest의 인자가 같은데 CoordinateDto통일은 어떤지..
public record CoordinateResponse(Double latitude,
                                 Double longitude) {
    public static CoordinateResponse of(final Position position) {
        return new CoordinateResponse( position.getLatitude().doubleValue(), position.getLongitude().doubleValue());
    }
}
