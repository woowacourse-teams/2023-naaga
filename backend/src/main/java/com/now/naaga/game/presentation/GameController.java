package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.presentation.dto.CreateGameRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RequestMapping("/games")
@RestController
public class GameController {

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(@Auth final PlayerRequest playerRequest,
                                                   @RequestBody final CreateGameRequest createGameRequest) {
        final CreateGameCommand createGameCommand = CreateGameCommand.of(playerRequest, createGameRequest);
        final Game game = gameService.createGame(createGameCommand);
        final GameResponse gameResponse = GameResponse.from(game);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/games/" + game.getId()))
                .body(gameResponse);
    }
    
    @PatchMapping("/{gameId}")
    public ResponseEntity<GameStatusResponse> endGame(@Auth final PlayerRequest playerRequest,
            @RequestBody final EndGameRequest endGameRequest,
            @PathVariable final Long gameId) {
        gameService.endGame(EndGameCommand.of(playerRequest, endGameRequest, gameId));
        Game game = gameService.findGameById(FindGameByIdCommand.of(playerRequest,gameId));
        return ResponseEntity.ok(GameStatusResponse.from(game));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findGame(@Auth final PlayerRequest playerRequest,
                                                 @PathVariable final Long gameId) {
        final FindGameByIdCommand findGameByIdCommand = FindGameByIdCommand.of(playerRequest, gameId);
        final Game game = gameService.findGameById(findGameByIdCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GameResponse.from(game));
    }

    @GetMapping
    public ResponseEntity<List<GameResponse>> findGamesByGameStatus(@Auth final PlayerRequest playerRequest,
                                                                    @RequestParam final String status) {
        final FindGameByStatusCommand findGameByStatusCommand = FindGameByStatusCommand.of(playerRequest, status);
        final List<Game> games = gameService.findGamesByStatus(findGameByStatusCommand);
        final List<GameResponse> gameResponses = GameResponse.convertToGameResponses(games);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameResponses);
    }
}
