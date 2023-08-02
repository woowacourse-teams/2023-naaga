package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.application.dto.FinishGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.presentation.dto.*;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.game.exception.GameExceptionType.INVALID_QUERY_PARAMETERS;

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
    public ResponseEntity<GameStatusResponse> changeGameStatus(@Auth final PlayerRequest playerRequest,
                                                               @RequestBody final FinishGameRequest finishGameRequest,
                                                               @PathVariable final Long gameId) {
        final FinishGameCommand finishGameCommand = FinishGameCommand.of(playerRequest, finishGameRequest, gameId);
        final Game game = gameService.finishGame(finishGameCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GameStatusResponse.from(game));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameResponse> findGame(@Auth final PlayerRequest playerRequest,
                                                 @PathVariable final Long gameId) {
        final FindGameByIdCommand findGameByIdCommand = FindGameByIdCommand.of(playerRequest, gameId);
        final Game game = gameService.findGame(findGameByIdCommand);
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

    @GetMapping("/{gameId}/result")
    public ResponseEntity<GameResultResponse> findGameResult(@Auth final PlayerRequest playerRequest,
                                                             @PathVariable final Long gameId) {
        final GameRecord gameRecord = gameService.findGameResult(gameId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(GameResultResponse.from(gameRecord));
    }

    @GetMapping("/results")
    public ResponseEntity<List<GameResultResponse>> findAllGameResult(@Auth final PlayerRequest playerRequest,
                                                                      @RequestParam final String sortBy,
                                                                      @RequestParam final String order) {
        if (!sortBy.equals("time") && order.equals("descending")) {
            throw new GameException(INVALID_QUERY_PARAMETERS);
        }

        final List<GameRecord> gameRecords = gameService.findAllGameResult();
        final List<GameResultResponse> gameResultResponseList = gameRecords.stream()
                .map(GameResultResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameResultResponseList);
    }
}
