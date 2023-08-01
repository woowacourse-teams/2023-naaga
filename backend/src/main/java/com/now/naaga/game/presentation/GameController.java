package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.HintService;
import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.application.dto.FinishGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.presentation.dto.CreateGameRequest;
import com.now.naaga.game.presentation.dto.CreateHintRequest;
import com.now.naaga.game.presentation.dto.FinishGameRequest;
import com.now.naaga.game.presentation.dto.GameResponse;
import com.now.naaga.game.presentation.dto.GameStatusResponse;
import com.now.naaga.game.presentation.dto.HintResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import java.util.List;
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
    public ResponseEntity<GameStatusResponse> changeGameStatus(@Auth final PlayerRequest playerRequest,
                                                               @RequestBody final FinishGameRequest finishGameRequest,
                                                               @PathVariable final Long gameId) {
        // TODO: 8/1/23 FinishGameCommand에 EndType 추가해야함
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
}
