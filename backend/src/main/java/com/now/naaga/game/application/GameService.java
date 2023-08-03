package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.game.exception.GameExceptionType.*;

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
        RecommendPlaceCommand recommendPlaceCommand = new RecommendPlaceCommand(position);
        final Place place = placeService.recommendPlaceByPosition(recommendPlaceCommand);
        final Game game = new Game(player, place, position);
        return gameRepository.save(game);
    }

    public void endGame(final EndGameCommand endGameCommand) {
        final Game game = gameRepository.findById(endGameCommand.gameId())
                .orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(endGameCommand.playerId());
        game.validateOwner(player);
        ResultType resultType = game.endGame(endGameCommand.endType(), endGameCommand.position());
        gameResultRepository.save(createGameResult(resultType, game));
    }

    private GameResult createGameResult(final ResultType resultType, final Game game) {
        Score score = scorePolicy.calculate(game);
        return new GameResult(resultType, score, game);
    }

    @Transactional(readOnly = true)
    public Game findGameById(final FindGameByIdCommand findGameByIdCommand) {
        final Player player = playerService.findPlayerById(findGameByIdCommand.playerId());
        final Game game = gameRepository.findById(findGameByIdCommand.gameId())
                .orElseThrow(() -> new GameException(NOT_EXIST));
        game.validateOwner(player);
        return game;
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByStatus(final FindGameByStatusCommand findGameByStatusCommand) {
        Player player = playerService.findPlayerById(findGameByStatusCommand.playerId());
        return gameRepository.findByPlayerIdAndGameStatus(player.getId(), findGameByStatusCommand.gameStatus());
    }

    @Transactional(readOnly = true)
    public GameResult findGameResultByGameId(final Long gameId) {
        final List<GameResult> gameResultsByGameId = gameResultRepository.findByGameId(gameId);

        if (gameResultsByGameId.isEmpty()) {
            throw new GameException(GAME_RESULT_NOT_EXIST);
        }

        return gameResultsByGameId.get(0);
    }

    @Transactional(readOnly = true)
    public GameRecord findGameResult(final Long gameId) {
        final GameResult gameResult = findGameResultByGameId(gameId);
        return GameRecord.from(gameResult);
    }

    @Transactional(readOnly = true)
    public List<GameRecord> findAllGameResult(final PlayerRequest playerRequest) {
        final List<Game> gamesByPlayerId = gameRepository.findByPlayerId(playerRequest.playerId());
        final List<GameResult> gameResults = gamesByPlayerId.stream()
                .map(game -> findGameResultByGameId(game.getId()))
                .sorted((gr1, gr2) -> gr2.getCreatedAt().compareTo(gr1.getCreatedAt()))
                .toList();

        return gameResults.stream()
                .map(GameRecord::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Statistic findStatistic(final PlayerRequest playerRequest) {
        final List<Game> gamesByPlayerId = gameRepository.findByPlayerId(playerRequest.playerId());
        final List<GameResult> gameResults = gamesByPlayerId.stream()
                .map(game -> findGameResultByGameId(game.getId()))
                .toList();
        final List<GameRecord> gameRecords = gameResults.stream()
                .map(GameRecord::from).toList();

        return Statistic.of(gameRecords);
    }
}
