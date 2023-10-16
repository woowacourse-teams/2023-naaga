package com.now.naaga.letter.application.dto;

import com.now.naaga.letter.presentation.dto.LetterRequest;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record CreateLetterCommand(Long playerId,
                                  String message,
                                  Position position) {

    public static CreateLetterCommand of(final PlayerRequest playerRequest,
                                         final LetterRequest letterRequest) {
        final Long playerId = playerRequest.playerId();
        final Position position = Position.of(letterRequest.latitude(), letterRequest.longitude());
        return new CreateLetterCommand(playerId,
                letterRequest.message(),
                position);
    }
}
