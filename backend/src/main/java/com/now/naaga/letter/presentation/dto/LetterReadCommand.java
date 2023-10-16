package com.now.naaga.letter.presentation.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record LetterReadCommand(Long playerId,
                                Long letterId) {

    public static LetterReadCommand of(final PlayerRequest playerRequest,
                                       final Long letterId) {
        final Long playerId = playerRequest.playerId();
        return new LetterReadCommand(playerId, letterId);
    }
}
