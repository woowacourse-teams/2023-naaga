package com.now.naaga.letter.application.letterlog.dto;

import com.now.naaga.letter.domain.Letter;

public record LetterLogCreateCommand(Long playerId,
                                     Letter letter) {
}
