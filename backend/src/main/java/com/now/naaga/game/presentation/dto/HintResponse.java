package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.place.domain.Position;

public record HintResponse(Long id,
                           Direction direction,
                           Position coordinate) {

    public static HintResponse from(final Hint hint) {
        return new HintResponse(hint.getId(), hint.getDirection(), hint.getPosition());
    }
}
