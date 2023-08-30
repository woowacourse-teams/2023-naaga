package com.now.naaga.game.domain;

import com.now.naaga.game.application.dto.CreateGameResultCommand;

public interface GameFinishService {

    void createGameResult(final CreateGameResultCommand createGameResultCommand);
}
