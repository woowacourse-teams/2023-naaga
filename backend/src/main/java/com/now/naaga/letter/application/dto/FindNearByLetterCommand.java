package com.now.naaga.letter.application.dto;

import com.now.naaga.place.domain.Position;

import java.math.BigDecimal;

public record FindNearByLetterCommand(Position position) {

    public static FindNearByLetterCommand from(final Double latitude,
                                               final Double longitude) {
        return new FindNearByLetterCommand(new Position(new BigDecimal(latitude), new BigDecimal(longitude)));
    }
}
