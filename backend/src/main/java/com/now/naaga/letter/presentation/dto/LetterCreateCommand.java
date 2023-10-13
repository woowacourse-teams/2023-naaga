package com.now.naaga.letter.presentation.dto;

import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record LetterCreateCommand(Long playerId,
                                  String message,
                                  Position position) {

    public static LetterCreateCommand of(final PlayerRequest playerRequest,
                                         final LetterRequest letterRequest) {
        final Long playerId = playerRequest.playerId();
        final Position position = Position.of(letterRequest.latitude(), letterRequest.longitude());
        return new LetterCreateCommand(playerId,
                letterRequest.message(),
                position);
    }
}
