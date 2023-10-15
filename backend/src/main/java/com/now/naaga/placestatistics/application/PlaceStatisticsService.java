package com.now.naaga.placestatistics.application;

import static com.now.naaga.placestatistics.domain.PlaceStatistics.LIKE_COUNT_DEFAULT_VALUE;

import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.application.dto.CreatePlaceStatisticsCommand;
import com.now.naaga.placestatistics.application.dto.SubtractLikeCommand;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.exception.PlaceStatisticsException;
import com.now.naaga.placestatistics.exception.PlaceStatisticsExceptionType;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlaceStatisticsService {

    private final PlaceStatisticsRepository placeStatisticsRepository;

    private final PlaceService placeService;

    public PlaceStatisticsService(final PlaceStatisticsRepository placeStatisticsRepository,
                                  final PlaceService placeService) {
        this.placeStatisticsRepository = placeStatisticsRepository;
        this.placeService = placeService;
    }

    public PlaceStatistics createPlaceStatistics(final CreatePlaceStatisticsCommand createPlaceStatisticsCommand) {
        final Long placeId = createPlaceStatisticsCommand.placeId();
        final Place place = placeService.findPlaceById(new FindPlaceByIdCommand(placeId));

        final PlaceStatistics placeStatistics = new PlaceStatistics(place, LIKE_COUNT_DEFAULT_VALUE);
        return placeStatisticsRepository.save(placeStatistics);
    }

    public void subtractLike(final SubtractLikeCommand subtractLikeCommand) {
        final Long placeId = subtractLikeCommand.placeId();

        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(placeId)
                                                                         .orElseThrow(() -> new PlaceStatisticsException(PlaceStatisticsExceptionType.NOT_FOUND));

        placeStatistics.subtractLike();
    }
}
