package com.now.naaga.like.application;

import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.exception.PlaceLikeExceptionType;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.application.PlaceStatisticsService;
import com.now.naaga.placestatistics.application.dto.PlusLikeCommand;
import com.now.naaga.placestatistics.application.dto.SubtractLikeCommand;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;

    private final PlaceStatisticsService placeStatisticsService;

    private final PlayerService playerService;

    private final PlaceService placeService;

    public PlaceLikeService(final PlaceLikeRepository placeLikeRepository,
                            final PlaceStatisticsService placeStatisticsService,
                            final PlayerService playerService,
                            final PlaceService placeService) {
        this.placeLikeRepository = placeLikeRepository;
        this.placeStatisticsService = placeStatisticsService;
        this.playerService = playerService;
        this.placeService = placeService;
    }

    public PlaceLike applyLike(final ApplyLikeCommand applyLikeCommand) {
        final Long playerId = applyLikeCommand.playerId();
        final Long placeId = applyLikeCommand.placeId();
        final PlaceLikeType placeLikeType = applyLikeCommand.placeLikeType();

        placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId)
                           .ifPresentOrElse(placeLike -> updatePlaceLike(placeLike, placeLikeType),
                                            () -> createPlaceLike(playerId, placeId, placeLikeType));

        return placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId).get();
    }

    private void updatePlaceLike(final PlaceLike placeLike,
                                 final PlaceLikeType placeLikeType) {
        if (placeLike.getType() == placeLikeType) {
            throw new PlaceLikeException(PlaceLikeExceptionType.ALREADY_APPLIED_TYPE);
        }
        placeLike.switchType();
        if (placeLikeType == PlaceLikeType.LIKE) {
            placeStatisticsService.plusLike(new PlusLikeCommand(placeLike.getPlace().getId()));
        } else {
            placeStatisticsService.subtractLike(new SubtractLikeCommand(placeLike.getPlace().getId()));
        }
    }

    private void createPlaceLike(final Long playerId,
                                 final Long placeId,
                                 final PlaceLikeType placeLikeType) {
        final Player player = playerService.findPlayerById(playerId);
        final Place place = placeService.findPlaceById(new FindPlaceByIdCommand(placeId));
        placeLikeRepository.save(new PlaceLike(place, player, placeLikeType));
        if (placeLikeType == PlaceLikeType.LIKE) {
            placeStatisticsService.plusLike(new PlusLikeCommand(placeId));
        }
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
