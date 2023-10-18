package com.now.naaga.letter.application.letterlog.dto;


import com.now.naaga.player.presentation.dto.PlayerRequest;

public record LetterByGameCommand(Long playerId,
                                  Long gameId) {

    public static LetterByGameCommand of(final PlayerRequest playerRequest,
                                         final Long gameId) {
        return new LetterByGameCommand(playerRequest.playerId(), gameId);
    }
}
