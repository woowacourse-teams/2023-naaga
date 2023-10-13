package com.now.naaga.letter.application.letterlog.dto;

import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;

public record WriteLetterLogCreateCommand(Game game,
                                          Letter letter) {
}
