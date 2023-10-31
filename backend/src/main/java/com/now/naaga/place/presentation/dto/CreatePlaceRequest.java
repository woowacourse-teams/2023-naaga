package com.now.naaga.place.presentation.dto;

public record CreatePlaceRequest(String name,
                                 String description,
                                 Double latitude,
                                 Double longitude,
                                 String imageUrl,
                                 Long registeredPlayerId,
                                 Long temporaryPlaceId) {
}
