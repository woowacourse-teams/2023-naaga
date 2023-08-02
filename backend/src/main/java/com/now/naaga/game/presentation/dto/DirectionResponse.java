package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Direction;

public record DirectionResponse(String direction) {

    public static DirectionResponse from(final Direction direction) {
        return new DirectionResponse(direction.name());
    }
}
