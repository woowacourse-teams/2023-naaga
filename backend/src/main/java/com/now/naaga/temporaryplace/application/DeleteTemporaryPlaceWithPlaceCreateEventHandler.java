package com.now.naaga.temporaryplace.application;

import com.now.naaga.place.domain.PlaceCreateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class DeleteTemporaryPlaceWithPlaceCreateEventHandler {

    private final TemporaryPlaceService temporaryPlaceService;

    public DeleteTemporaryPlaceWithPlaceCreateEventHandler(final TemporaryPlaceService temporaryPlaceService) {
        this.temporaryPlaceService = temporaryPlaceService;
    }

    @Transactional
    @EventListener
    public void handle(final PlaceCreateEvent placeCreateEvent) {
        final Long temporaryPlaceId = placeCreateEvent.temporaryPlaceId();
        temporaryPlaceService.deleteByIdWhenPlaceCreated(temporaryPlaceId);
    }
}
