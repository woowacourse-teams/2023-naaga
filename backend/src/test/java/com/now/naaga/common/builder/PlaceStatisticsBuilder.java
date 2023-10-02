package com.now.naaga.common.builder;

import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PlaceStatisticsBuilder {

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private PlaceBuilder placeBuilder;

    private Long likeCount;

    private Optional<Place> place;

    public PlaceStatisticsBuilder init() {
        this.place = Optional.empty();
        this.likeCount = 0L;
        return this;
    }

    public PlaceStatisticsBuilder likeCount(final Long likeCount) {
        this.likeCount = likeCount;
        return this;
    }

    public PlaceStatisticsBuilder place(final Place persistedPlace) {
        this.place = Optional.ofNullable(persistedPlace);
        return this;
    }

    public PlaceStatistics build() {
        final Place persistePlace = place.orElseGet(this::getPersistedPlace);
        final PlaceStatistics placeStatistics = new PlaceStatistics(persistePlace, likeCount);
        return placeStatisticsRepository.save(placeStatistics);
    }

    private Place getPersistedPlace() {
        return placeBuilder.init()
                           .build();
    }
}
