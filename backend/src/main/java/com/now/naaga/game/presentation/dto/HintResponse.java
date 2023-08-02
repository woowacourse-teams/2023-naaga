package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Hint;
import com.now.naaga.place.presentation.dto.CoordinateResponse;

public record HintResponse(Long id,
                           DirectionResponse direction,
                           CoordinateResponse coordinate) {

    public static HintResponse from(final Hint hint) {
        final DirectionResponse directionResponse = DirectionResponse.from(hint.getDirection());
        final CoordinateResponse coordinateResponse = CoordinateResponse.from(hint.getPosition());
        return new HintResponse(hint.getId(), directionResponse, coordinateResponse);
    }
}
