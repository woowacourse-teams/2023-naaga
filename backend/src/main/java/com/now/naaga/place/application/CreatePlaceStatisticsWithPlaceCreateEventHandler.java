package com.now.naaga.place.application;

import com.now.naaga.place.application.dto.CreatePlaceStatisticsCommand;
import com.now.naaga.place.domain.PlaceCreateEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class CreatePlaceStatisticsWithPlaceCreateEventHandler {

    private final PlaceStatisticsService placeStatisticsService;

    public CreatePlaceStatisticsWithPlaceCreateEventHandler(final PlaceStatisticsService placeStatisticsService) {
        this.placeStatisticsService = placeStatisticsService;
    }

    @EventListener
    public void handle(final PlaceCreateEvent placeCreateEvent) {
        final Long placeId = placeCreateEvent.placeId();
        final CreatePlaceStatisticsCommand command = new CreatePlaceStatisticsCommand(placeId);
        placeStatisticsService.createPlaceStatistics(command);
    }
}
