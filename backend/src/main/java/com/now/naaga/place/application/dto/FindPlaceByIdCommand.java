package com.now.naaga.place.application.dto;

public record FindPlaceByIdCommand(Long placeId) {

    public static FindPlaceByIdCommand from(final Long placeId) {

        return new FindPlaceByIdCommand(placeId);
    }
}
