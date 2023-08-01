package com.now.naaga.place.application.dto;

import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindPlaceByIdCommand(Long placeId) {

    public static FindPlaceByIdCommand from(final Long placeId) {

        return new FindPlaceByIdCommand(placeId);
    }
}
