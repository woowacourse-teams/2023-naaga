package com.now.naaga.player.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.player.presentation.dto.RankResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;

@RequestMapping("/ranks")
@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/my")
    public ResponseEntity<RankResponse> findMyRank(@Auth final PlayerRequest playerRequest) {
        final Rank rank = playerService.getRankAndTopPercent(playerRequest);
        final RankResponse rankResponse = RankResponse.of(rank);
        return ResponseEntity.ok(rankResponse);
    }

    @GetMapping
    public ResponseEntity<List<RankResponse>> findAllRank(@RequestParam(name = "sort-by") final String sortBy,
                                                          @RequestParam(name = "order") final String order) {
        if (!sortBy.equalsIgnoreCase("RANK") || !order.equalsIgnoreCase("ASCENDING")) {
            throw new CommonException(INVALID_REQUEST_PARAMETERS);
        }

        final List<Rank> ranks = playerService.getAllPlayersByRanksAscending();
        final List<RankResponse> rankResponseList = ranks.stream()
                .map(RankResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rankResponseList);
    }

}
