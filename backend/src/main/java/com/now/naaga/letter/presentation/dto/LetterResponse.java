package com.now.naaga.letter.presentation.dto;

import com.now.naaga.game.presentation.dto.CoordinateResponse;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.player.presentation.dto.PlayerResponse;

public record LetterResponse(Long id,
                             PlayerResponse player,
                             CoordinateResponse coordinate,
                             String message,
                             String registerDate) {

    public static LetterResponse from(final Letter letter) {
        final Long id = letter.getId();
        final PlayerResponse playerResponse = PlayerResponse.from(letter.getRegisteredPlayer());
        final CoordinateResponse coordinateResponse = CoordinateResponse.of(letter.getPosition());
        final String registerDate = letter.getCreatedTime().toString();
        return new LetterResponse(id, playerResponse, coordinateResponse, letter.getMessage(), registerDate);
    }
}
