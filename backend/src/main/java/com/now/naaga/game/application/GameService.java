package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.application.dto.FinishGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.domain.ResultType;
import com.now.naaga.game.domain.ScorePolicy;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.game.exception.GameExceptionType.ALREADY_IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;
    
    private final GameResultRepository gameResultRepository;

    private final PlayerService playerService;

    private final PlaceService placeService;
    
    private final ScorePolicy scorePolicy;
    
    public GameService(GameRepository gameRepository, GameResultRepository gameResultRepository,
            PlayerService playerService, PlaceService placeService, ScorePolicy scorePolicy) {
        this.gameRepository = gameRepository;
        this.gameResultRepository = gameResultRepository;
        this.playerService = playerService;
        this.placeService = placeService;
        this.scorePolicy = scorePolicy;
    }
    
    public Game createGame(final CreateGameCommand createGameCommand) {
        final Player player = playerService.findPlayerById(createGameCommand.playerId());
        final List<Game> gamesByStatus = gameRepository.findByPlayerIdAndGameStatus(player.getId(), GameStatus.IN_PROGRESS);
        if (!gamesByStatus.isEmpty()) {
            throw new GameException(ALREADY_IN_PROGRESS);
        }
        final Position position = createGameCommand.playerPosition();
        final Place place = placeService.recommendPlaceByPosition(position);
        final Game game = new Game(player, place, position);
        return gameRepository.save(game);
    }
    
    public void endGame(final EndGameCommand endGameCommand) {
        final Game game = gameRepository.findById(endGameCommand.gameId())
                .orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(endGameCommand.playerId());
        game.validatePlayer(player);
        ResultType resultType = game.endGame(endGameCommand.endType(), endGameCommand.position());
        gameResultRepository.save(createGameResult(resultType, game));
    }
    
    private GameResult createGameResult(final ResultType resultType, final Game game) {
        Score score = scorePolicy.calculate(game);
        return new GameResult(resultType, score, game);
    }

    @Transactional(readOnly = true)
    public Game findGame(final FindGameByIdCommand findGameByIdCommand) {
        final Player player = playerService.findPlayerById(findGameByIdCommand.playerId());
        final Game game = gameRepository.findById(findGameByIdCommand.gameId())
                .orElseThrow(() -> new GameException(NOT_EXIST));
        game.validatePlayer(player);
        return game;
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByStatus(final FindGameByStatusCommand findGameByStatusCommand) {
        Player player = playerService.findPlayerById(findGameByStatusCommand.playerId());
        return gameRepository.findByPlayerIdAndGameStatus(player.getId(), findGameByStatusCommand.gameStatus());
    }
}
