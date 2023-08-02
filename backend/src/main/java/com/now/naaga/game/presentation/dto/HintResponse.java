package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Hint;
import com.now.naaga.place.presentation.dto.CoordinateResponse;

public record HintResponse(Long id,
                           String direction,
                           CoordinateResponse coordinate) {

    public static HintResponse from(final Hint hint) {
        final CoordinateResponse coordinateResponse = CoordinateResponse.from(hint.getPosition());
        return new HintResponse(
                hint.getId(),
                hint.getDirection().name(),
                coordinateResponse);
    }
}
