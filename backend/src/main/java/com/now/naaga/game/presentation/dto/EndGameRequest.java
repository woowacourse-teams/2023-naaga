package com.now.naaga.game.presentation.dto;

import com.now.naaga.game.domain.EndType;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record EndGameRequest(String endType, CoordinateRequest coordinate) {

}
