package com.now.naaga.game.application.dto;

import com.now.naaga.place.domain.Position;

public record CreateHintCommand(Long memberId, Long gameId, Position coordinate) {

}
