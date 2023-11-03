package com.now.naaga.place.application;

import static com.now.naaga.place.domain.PlaceStatistics.LIKE_COUNT_DEFAULT_VALUE;

import com.now.naaga.place.application.dto.CreatePlaceStatisticsCommand;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.FindPlaceStatisticsByPlaceIdCommand;
import com.now.naaga.place.application.dto.PlusLikeCommand;
import com.now.naaga.place.application.dto.SubtractLikeCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.place.exception.PlaceStatisticsException;
import com.now.naaga.place.exception.PlaceStatisticsExceptionType;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
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

    public void plusLike(final PlusLikeCommand plusLikeCommand) {
        final Long placeId = plusLikeCommand.placeId();
        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(placeId)
                                                                         .orElseThrow(() -> new PlaceException(PlaceExceptionType.NO_EXIST));
        placeStatistics.plusLike();
    }

    public void subtractLike(final SubtractLikeCommand subtractLikeCommand) {
        final Long placeId = subtractLikeCommand.placeId();

        final PlaceStatistics placeStatistics = placeStatisticsRepository.findByPlaceId(placeId)
                                                                         .orElseThrow(() -> new PlaceStatisticsException(PlaceStatisticsExceptionType.NOT_FOUND));

        placeStatistics.subtractLike();
    }

    public PlaceStatistics findPlaceStatisticsByPlaceId(final FindPlaceStatisticsByPlaceIdCommand findPlaceStatisticsByPlaceIdCommand) {
        final Long placeId = findPlaceStatisticsByPlaceIdCommand.placeId();

        return placeStatisticsRepository.findByPlaceId(placeId)
                                        .orElseThrow(() -> new PlaceStatisticsException(PlaceStatisticsExceptionType.NOT_FOUND));
    }
}
