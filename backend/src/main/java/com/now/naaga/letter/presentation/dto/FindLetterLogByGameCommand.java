package com.now.naaga.letter.presentation.dto;

import com.now.naaga.letter.presentation.LogType;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindLetterLogByGameCommand(Long playerId,
                                         Long gameId,
                                         LogType logType) {

    public static FindLetterLogByGameCommand of(final PlayerRequest playerRequest,
                                                final Long gameId,
                                                final String logType) {
        return new FindLetterLogByGameCommand(playerRequest.playerId(), gameId, LogType.valueOf(logType));
    }
}

