package com.now.naaga.letter.presentation.dto;

import com.now.naaga.letter.domain.letterlog.LetterLogType;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindLetterLogByGameCommand(Long playerId,
                                         Long gameId,
                                         LetterLogType letterLogType) {

    public static FindLetterLogByGameCommand of(final PlayerRequest playerRequest,
                                                final Long gameId,
                                                final LetterLogType logType) {
        return new FindLetterLogByGameCommand(playerRequest.playerId(), gameId, logType);
    }
}

