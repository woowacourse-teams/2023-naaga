package com.now.naaga.letter.presentation.dto;

import com.now.naaga.game.presentation.dto.CoordinateResponse;
import com.now.naaga.letter.domain.Letter;

public record NearByLetterResponse(Long id,
                                   CoordinateResponse coordinateResponse) {

    public static NearByLetterResponse from(final Letter letter) {
        return new NearByLetterResponse(letter.getId(), CoordinateResponse.of(letter.getPosition()));
    }
}
