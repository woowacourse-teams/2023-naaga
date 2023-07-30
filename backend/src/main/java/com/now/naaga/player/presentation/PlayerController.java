package com.now.naaga.player.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Players;
import com.now.naaga.player.presentation.dto.RankResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequestMapping("/ranks")
@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/my")
    public ResponseEntity<RankResponse> findMyRank(@Auth final MemberCommand memberCommand) {
        final Players players = playerService.findAllPlayersByRanksAscending();
        final Player player = playerService.getRankAndTopPercent(memberCommand);

        final int rank = players.calculateRank(player);
        final double topPercent = players.calculateTopPercent(rank);

        final RankResponse rankResponse = RankResponse.to(player,rank,topPercent);
        return ResponseEntity.ok(rankResponse);
    }

    @GetMapping
    public ResponseEntity<List<RankResponse>> findAllRank(@Auth final MemberCommand memberCommand,
                                                          @RequestParam final String sortBy,
                                                          @RequestParam final String order) {
        final Players players = playerService.findAllPlayersByRanksAscending();
        final List<RankResponse> rankResponseList = new ArrayList<>();
        for (Player player : players.getPlayers()) {
            final int rank = players.calculateRank(player);
            final double topPercent = players.calculateTopPercent(rank);
            rankResponseList.add(RankResponse.to(player, rank, topPercent));
        }

        return ResponseEntity.ok(rankResponseList);
    }
}
