package com.now.naaga.player.presentation.dto;

import com.now.naaga.player.domain.Player;

public record EditPlayerResponse(String nickname) {

    public static EditPlayerResponse from(final Player player) {
        return new EditPlayerResponse(player.getNickname());
    }
}
