package com.now.naaga.game.presentation;

import static com.now.naaga.game.exception.GameExceptionType.INVALID_QUERY_PARAMETERS;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.HintService;
import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.presentation.dto.CreateGameRequest;
import com.now.naaga.game.presentation.dto.CreateHintRequest;
import com.now.naaga.game.presentation.dto.EndGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameResultResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.presentation.dto.HintResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/games")
@RestController
public class GameController {

    private final GameService gameService;
    private final HintService hintService;

    public GameController(final GameService gameService, final HintService hintService) {
        this.gameService = gameService;
        this.hintService = hintService;
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

    @PostMapping("/{gameId}/hints")
    public ResponseEntity<HintResponse> createHint(@Auth final PlayerRequest playerRequest,
                                                   @RequestBody final CreateHintRequest createHintRequest,
                                                   @PathVariable final Long gameId) {
        final CreateHintCommand createHintCommand = CreateHintCommand.of(playerRequest, createHintRequest, gameId);
        final Hint hint = hintService.createHint(createHintCommand);
        final HintResponse hintResponse = HintResponse.from(hint);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/games/" + gameId + "/hints/" + hint.getId()))
                .body(hintResponse);
    }

    @PatchMapping("/{gameId}")
    public ResponseEntity<GameStatusResponse> endGame(@Auth final PlayerRequest playerRequest,
                                                      @RequestBody final EndGameRequest endGameRequest,
                                                      @PathVariable final Long gameId) {
        gameService.endGame(EndGameCommand.of(playerRequest, endGameRequest, gameId));
        Game game = gameService.findGameById(FindGameByIdCommand.of(playerRequest, gameId));
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
                                                                      @RequestParam(name = "sort-by") final String sortBy,
                                                                      @RequestParam(name = "order") final String order) {
        if (!sortBy.equalsIgnoreCase("TIME") || !order.equalsIgnoreCase("DESCENDING")) {
            throw new GameException(INVALID_QUERY_PARAMETERS);
        }

        final List<GameRecord> gameRecords = gameService.findAllGameResult(playerRequest);
        final List<GameResultResponse> gameResultResponseList = gameRecords.stream()
                .map(GameResultResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(gameResultResponseList);
    }
}
