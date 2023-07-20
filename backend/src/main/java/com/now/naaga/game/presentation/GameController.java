package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.place.domain.Position;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/games")
@RestController
public class GameController {

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Void> createGame(@Auth final MemberCommand memberCommand,
                                           @RequestBody final Position position) {
        final Game game = gameService.createGame(memberCommand, position);
        return ResponseEntity.created(URI.create("/games/" + game.getId())).build();
    }

    @PatchMapping("/{gameId}")
    public ResponseEntity<GameStatusResponse> changeGameStatus(@Auth final MemberCommand memberCommand,
                                                               @RequestBody final Position position,
                                                               @PathVariable final Long gameId) {
        final Game game = gameService.finishGame(memberCommand, position, gameId);
        return ResponseEntity.ok(GameStatusResponse.from(game));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findGame(@Auth final MemberCommand memberCommand,
                                                 @PathVariable final Long gameId) {
        final Game game = gameService.findGame(memberCommand, gameId);
        return ResponseEntity.ok(GameResponse.from(game));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> findGamesByGameStatus(@Auth final MemberCommand memberCommand,
                                                                    @RequestParam final String gameStatus) {
        final List<Game> games = gameService.findGamesByStatus(memberCommand, gameStatus);
        final List<GameResponse> gameResponses = games.stream()
                .map(GameResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(gameResponses);
    }
}
