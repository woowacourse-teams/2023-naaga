package com.now.naaga.player.application.dto;

import com.now.naaga.score.domain.Score;

public record AddScoreCommand(Long playerId,
                              Score score) {
}
