package com.now.naaga.like.application;

import com.now.naaga.like.application.dto.CancelLikeCommand;
import com.now.naaga.like.domain.PlaceLike;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.exception.PlaceLikeExceptionType;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlaceLikeService {

    private final PlaceLikeRepository placeLikeRepository;

    private final PlayerService playerService;

    public PlaceLikeService(final PlaceLikeRepository placeLikeRepository,
                            final PlayerService playerService) {
        this.placeLikeRepository = placeLikeRepository;
        this.playerService = playerService;
    }

    public void cancelLike(final CancelLikeCommand cancelLikeCommand) {
        final Long playerId = cancelLikeCommand.playerId();
        final Player player = playerService.findPlayerById(playerId);

        final Long placeId = cancelLikeCommand.placeId();
        final PlaceLike placeLike = placeLikeRepository.findByPlaceIdAndPlayerId(placeId, playerId)
                .orElseThrow(() -> new PlaceLikeException(PlaceLikeExceptionType.NOT_EXIST));

        placeLike.validateOwner(player);
        placeLikeRepository.delete(placeLike);
    }
}
