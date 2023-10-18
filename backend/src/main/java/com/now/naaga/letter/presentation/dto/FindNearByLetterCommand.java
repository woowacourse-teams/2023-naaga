package com.now.naaga.letter.presentation.dto;

import com.now.naaga.place.domain.Position;
import java.math.BigDecimal;

public record FindNearByLetterCommand(Position position) {

    public static FindNearByLetterCommand from(final Double latitude,
                                               final Double longitude) {
        return new FindNearByLetterCommand(new Position(BigDecimal.valueOf(latitude), BigDecimal.valueOf(longitude)));
    }
}
