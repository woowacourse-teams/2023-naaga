package com.now.naaga.player.application.dto;

import com.now.naaga.player.presentation.dto.EditPlayerRequest;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record EditPlayerNicknameCommand(Long playerId,
                                        String nickname) {

    public static EditPlayerNicknameCommand of(final PlayerRequest playerRequest,
                                               final EditPlayerRequest editPlayerRequest) {
        return new EditPlayerNicknameCommand(
                playerRequest.playerId(),
                editPlayerRequest.nickname()
        );
    }
}
