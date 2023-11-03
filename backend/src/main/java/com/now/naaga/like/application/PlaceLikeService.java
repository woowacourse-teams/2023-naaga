package com.now.naaga.like.application;

import com.now.naaga.like.application.dto.ApplyLikeCommand;
import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.application.dto.CheckMyPlaceLikeCommand;
import com.now.naaga.like.application.dto.CountPlaceLikeCommand;
import com.now.naaga.like.domain.MyPlaceLikeType;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.domain.PlaceLikeType;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.exception.PlaceLikeExceptionType;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.PlaceStatisticsService;
import com.now.naaga.place.application.dto.FindPlaceByIdCommand;
import com.now.naaga.place.application.dto.FindPlaceStatisticsByPlaceIdCommand;
import com.now.naaga.place.application.dto.PlusLikeCommand;
import com.now.naaga.place.application.dto.SubtractLikeCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
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

        if (placeLikeType == PlaceLikeType.LIKE) {
            placeStatisticsService.plusLike(new PlusLikeCommand(placeId));
        }

        final Optional<PlaceLike> maybePlaceLike = placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId);

        if (maybePlaceLike.isPresent()) {
            final PlaceLike exsistingPlaceLike = maybePlaceLike.get();
            return updatePlaceLike(exsistingPlaceLike, placeLikeType);
        }

        return createPlaceLike(playerId, placeId, placeLikeType);
    }

    private PlaceLike updatePlaceLike(final PlaceLike target,
                                      final PlaceLikeType toBeChanged) {
        if (target.getType() == toBeChanged) {
            throw new PlaceLikeException(PlaceLikeExceptionType.ALREADY_APPLIED_TYPE);
        }
        if (toBeChanged == PlaceLikeType.DISLIKE) {
            placeStatisticsService.subtractLike(new SubtractLikeCommand(target.getPlace().getId()));
        }
        target.switchType();
        return target;
    }

    private PlaceLike createPlaceLike(final Long playerId,
                                      final Long placeId,
                                      final PlaceLikeType placeLikeType) {
        final Player player = playerService.findPlayerById(playerId);
        final Place place = placeService.findPlaceById(new FindPlaceByIdCommand(placeId));
        return placeLikeRepository.save(new PlaceLike(place, player, placeLikeType));
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

        subtractPlaceLikeCount(placeId, placeLike);
    }

    private void subtractPlaceLikeCount(final Long placeId, final PlaceLike placeLike) {
        if (placeLike.getType() == PlaceLikeType.LIKE) {
            final SubtractLikeCommand subtractLikeCommand = new SubtractLikeCommand(placeId);
            placeStatisticsService.subtractLike(subtractLikeCommand);
        }
    }

    @Transactional(readOnly = true)
    public MyPlaceLikeType checkMyLike(final CheckMyPlaceLikeCommand checkMyPlaceLikeCommand) {
        final Long playerId = checkMyPlaceLikeCommand.playerId();
        final Long placeId = checkMyPlaceLikeCommand.placeId();

        return placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId)
                                  .map(PlaceLike::getType)
                                  .map(MyPlaceLikeType::from)
                                  .orElse(MyPlaceLikeType.NONE);
    }

    @Transactional(readOnly = true)
    public Long countPlaceLike(final CountPlaceLikeCommand countPlaceLikeCommand) {
        final FindPlaceStatisticsByPlaceIdCommand findPlaceStatisticsByPlaceIdCommand = new FindPlaceStatisticsByPlaceIdCommand(countPlaceLikeCommand.placeId());
        final PlaceStatistics placeStatistics = placeStatisticsService.findPlaceStatisticsByPlaceId(findPlaceStatisticsByPlaceIdCommand);

        return placeStatistics.getLikeCount();
    }
}
