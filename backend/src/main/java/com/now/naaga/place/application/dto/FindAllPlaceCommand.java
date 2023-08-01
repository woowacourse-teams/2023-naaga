package com.now.naaga.place.application.dto;

import com.now.naaga.common.domain.OrderType;
import com.now.naaga.place.domain.SortType;
import com.now.naaga.player.presentation.dto.PlayerRequest;

public record FindAllPlaceCommand(Long playerId,
                                  SortType sortType,
                                  OrderType orderType) {

    public static FindAllPlaceCommand of(final PlayerRequest playerRequest,
                          final String sortBy,
                          final String order) {
        return new FindAllPlaceCommand(
                playerRequest.playerId(),
                SortType.valueOf(sortBy.toUpperCase()),
                OrderType.valueOf(order.toUpperCase())
        );
    }
}
