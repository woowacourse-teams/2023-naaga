package com.now.naaga.game.presentation.dto;

import com.now.naaga.place.domain.Position;

import java.math.BigDecimal;

public record CoordinateRequest(Double latitude,
                                Double longitude) {

    public Position convertToPosition() {
        return new Position(new BigDecimal(latitude), new BigDecimal(longitude));
    }
}
