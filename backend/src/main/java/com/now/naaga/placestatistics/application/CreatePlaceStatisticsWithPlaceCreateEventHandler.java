package com.now.naaga.placestatistics.application;

import com.now.naaga.place.domain.PlaceCreateEvent;
import com.now.naaga.placestatistics.application.dto.CreatePlaceStatisticsCommand;
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
