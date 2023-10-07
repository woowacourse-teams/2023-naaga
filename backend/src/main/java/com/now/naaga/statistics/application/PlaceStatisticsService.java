package com.now.naaga.statistics.application;

import com.now.naaga.placestatistics.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import com.now.naaga.statistics.application.dto.SubtractLikeCommand;
import com.now.naaga.statistics.exception.PlaceStatisticsExceptionType;
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
}