package com.now.naaga.like.application;

import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.application.PlaceStatisticsService;
import com.now.naaga.place.application.dto.SubtractLikeCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;

    private final PlaceStatisticsService placeStatisticsService;

    public PlaceLikeService(final PlaceLikeRepository placeLikeRepository,
                            final PlaceStatisticsService placeStatisticsService) {
        this.placeLikeRepository = placeLikeRepository;
        this.placeStatisticsService = placeStatisticsService;
    }

    public void cancelLike(final CancelLikeCommand cancelLikeCommand) {
        final Long playerId = cancelLikeCommand.playerId();
        final Long placeId = cancelLikeCommand.placeId();

        final Optional<PlaceLike> maybePlaceLike = placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId);
        if (maybePlaceLike.isEmpty()) {
            return;
        }

        final PlaceLike placeLike = maybePlaceLike.get();
        placeLikeRepository.delete(placeLike);

        final SubtractLikeCommand subtractLikeCommand = new SubtractLikeCommand(placeId);
        placeStatisticsService.subtractLike(subtractLikeCommand);
    }
}
