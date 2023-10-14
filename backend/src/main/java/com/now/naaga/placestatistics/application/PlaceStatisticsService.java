package com.now.naaga.placestatistics.application;

import com.now.naaga.placestatistics.application.dto.FindPlaceStatisticsByPlaceIdCommand;
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

    public PlaceStatisticsService(final PlaceStatisticsRepository placeStatisticsRepository) {
        this.placeStatisticsRepository = placeStatisticsRepository;
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
