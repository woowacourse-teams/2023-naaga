package com.now.naaga.player.application.dto;

public record EditPlayerNicknameCommand(Long playerId,
                                        String nickname) {
}
