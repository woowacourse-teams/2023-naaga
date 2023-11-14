package com.now.naaga.player.presentation;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.application.dto.EditPlayerNicknameCommand;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.presentation.dto.*;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(final PlayerService playerService) {
        this.playerService = playerService;
    }

    @PatchMapping("/profiles/my")
    public ResponseEntity<EditPlayerResponse> editPlayer(@Auth final PlayerRequest playerRequest,
                                                         @RequestBody @Valid final EditPlayerRequest editPlayerRequest) {
        final EditPlayerNicknameCommand editPlayerNicknameCommand = EditPlayerNicknameCommand.of(playerRequest, editPlayerRequest);
        final Player player = playerService.editPlayerNickname(editPlayerNicknameCommand);
        final EditPlayerResponse editPlayerResponse = EditPlayerResponse.from(player);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(editPlayerResponse);
    }

    @GetMapping("/ranks/my")
    public ResponseEntity<RankResponse> findMyRank(@Auth final PlayerRequest playerRequest) {
        final Rank rank = playerService.getRankAndTopPercent(playerRequest);
        final RankResponse rankResponse = RankResponse.of(rank);
        return ResponseEntity.ok(rankResponse);
    }

    @GetMapping("/ranks")
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
