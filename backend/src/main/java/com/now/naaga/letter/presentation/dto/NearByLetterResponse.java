package com.now.naaga.letter.presentation.dto;

import com.now.naaga.game.presentation.dto.CoordinateResponse;
import com.now.naaga.letter.domain.Letter;
import java.util.List;

public record NearByLetterResponse(Long id,
                                   CoordinateResponse coordinate) {

    public static NearByLetterResponse from(final Letter letter) {
        return new NearByLetterResponse(letter.getId(), CoordinateResponse.of(letter.getPosition()));
    }

    public static List<NearByLetterResponse> convertToLetterResponses(final List<Letter> letters) {
        return letters.stream()
                .map(NearByLetterResponse::from)
                .toList();
    }
}
