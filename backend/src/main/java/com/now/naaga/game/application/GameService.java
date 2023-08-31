package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.*;
import com.now.naaga.game.domain.*;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameNotArrivalException;
import com.now.naaga.game.persistence.GameRepository;

// TODO: 8/31/23 제거할 대상 - 이슈 범위를 벗어나서 일단은 제거하지 않음
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.exception.GameResultException;
import com.now.naaga.gameresult.persistence.GameResultRepository;

import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.game.exception.GameExceptionType.*;

// TODO: 8/31/23 제거할 대상 - 이슈 범위를 벗어나서 일단은 제거하지 않음
import static com.now.naaga.gameresult.exception.GameResultExceptionType.GAME_RESULT_NOT_EXIST;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;

    // TODO: 8/31/23 제거할 대상 - 이슈 범위를 벗어나서 일단은 제거하지 않음
    private final GameResultRepository gameResultRepository;

    private final PlayerService playerService;

    private final PlaceService placeService;

    private final GameFinishService gameFinishService;

    public GameService(final GameRepository gameRepository,
            final GameResultRepository gameResultRepository,
            final PlayerService playerService,
            final PlaceService placeService,
            final GameFinishService gameFinishService) {
        this.gameRepository = gameRepository;
        this.gameResultRepository = gameResultRepository;
        this.playerService = playerService;
        this.placeService = placeService;
        this.gameFinishService = gameFinishService;
    }

    public Game createGame(final CreateGameCommand createGameCommand) {
        final Player player = playerService.findPlayerById(createGameCommand.playerId());
        final List<Game> gamesByStatus = gameRepository.findByPlayerIdAndGameStatus(player.getId(),
                GameStatus.IN_PROGRESS);
        if (!gamesByStatus.isEmpty()) {
            throw new GameException(ALREADY_IN_PROGRESS);
        }
        final Position position = createGameCommand.playerPosition();
        final RecommendPlaceCommand recommendPlaceCommand = new RecommendPlaceCommand(position);
        try {
            final Place place = placeService.recommendPlaceByPosition(recommendPlaceCommand);
            final Game game = new Game(player, place, position);
            return gameRepository.save(game);
        } catch (PlaceException exception) {
            throw new GameException(CAN_NOT_FIND_PLACE);
        }
    }

    public void endGame(final EndGameCommand endGameCommand) {
        final Long gameId = endGameCommand.gameId();
        final Long playerId = endGameCommand.playerId();
        final EndType endType = endGameCommand.endType();
        final Position position = endGameCommand.position();

        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(playerId);
        game.validateOwner(player);

        try {
            game.endGame(position, endType);
        } catch (final GameNotArrivalException gameNotArrivalException) {
            final SubtractAttemptsCommand subtractAttemptsCommand = new SubtractAttemptsCommand(gameId, playerId);
            subtractAttempts(subtractAttemptsCommand);
            throw new GameNotArrivalException(NOT_ARRIVED);
        }

        final CreateGameResultCommand createGameResultCommand = new CreateGameResultCommand(player, game, position, endType);
        gameFinishService.createGameResult(createGameResultCommand);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void subtractAttempts(final SubtractAttemptsCommand subtractAttemptsCommand) {
        final Long gameId = subtractAttemptsCommand.gameId();
        final Long playerId = subtractAttemptsCommand.playerId();

        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(playerId);
        game.validateOwner(player);

        game.subtractAttempts();
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
            throw new GameResultException(GAME_RESULT_NOT_EXIST);
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
        final List<Game> gamesByPlayerId = gameRepository.findByPlayerIdAndGameStatus(playerRequest.playerId(),
                GameStatus.DONE);
        final List<GameResult> gameResults = gamesByPlayerId.stream()
                .map(game -> findGameResultByGameId(game.getId()))
                .toList();
        final List<GameRecord> gameRecords = gameResults.stream()
                .map(GameRecord::from).toList();

        return Statistic.of(gameRecords);
    }

    @Transactional(readOnly = true)
    public List<Game> findAllGames(FindAllGamesCommand findAllGamesCommand) {
        return gameRepository.findByPlayerId(findAllGamesCommand.playerId());
    }
}
