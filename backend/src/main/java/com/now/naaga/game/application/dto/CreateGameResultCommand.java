package com.now.naaga.game.application.dto;

import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;

// TODO: 8/31/23 player -> ID / game -> game -> ID  
public record CreateGameResultCommand(Player player,
                                      Game game,
                                      Position position,
                                      EndType endType) {
}
