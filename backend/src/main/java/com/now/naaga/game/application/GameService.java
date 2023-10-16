package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.*;
import com.now.naaga.game.domain.EndType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.domain.Statistic;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameNotFinishedException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.gameresult.domain.GameResult;
import com.now.naaga.gameresult.exception.GameResultException;
import com.now.naaga.gameresult.repository.GameResultRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.application.dto.RecommendPlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.*;
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
                IN_PROGRESS);
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
    
    @Transactional(noRollbackFor = {GameNotFinishedException.class})
    public void endGame(final EndGameCommand endGameCommand) {
        final Game game = gameRepository.findById(endGameCommand.gameId()).orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(endGameCommand.playerId());
        game.validateOwner(player);
        
        final EndType endType = endGameCommand.endType();
        final Position position = endGameCommand.position();
        
        game.endGame(position, endType);
        
        final CreateGameResultCommand createGameResultCommand = new CreateGameResultCommand(player, game, position, endType);
        gameFinishService.createGameResult(createGameResultCommand);
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
        final Long playerId = playerRequest.playerId();
        final List<GameResult> gameResults = gameResultRepository.findByPlayerId(playerId);
        
        final List<GameResult> sortedGameResults = gameResults.stream()
                                                              .sorted((gr1, gr2) -> gr2.getCreatedAt().compareTo(gr1.getCreatedAt()))
                                                              .toList();
        
        return sortedGameResults.stream()
                                .map(GameRecord::from)
                                .toList();
    }
    
    @Transactional(readOnly = true)
    public Statistic findStatistic(final PlayerRequest playerRequest) {
        final List<GameResult> gameResults = gameResultRepository.findByPlayerId(playerRequest.playerId());
        final List<GameRecord> gameRecords = gameResults.stream()
                                                        .map(GameRecord::from).toList();
        
        return Statistic.of(gameRecords);
    }
    
    @Transactional(readOnly = true)
    public List<Game> findAllGames(FindAllGamesCommand findAllGamesCommand) {
        return gameRepository.findByPlayerId(findAllGamesCommand.playerId());
    }
    
    @Transactional(readOnly = true)
    public Game findGameInProgress(final FindGameInProgressCommand findGameByStatusCommand) {
        List<Game> gameInProgress = gameRepository.findByPlayerIdAndGameStatus(findGameByStatusCommand.playerId(), IN_PROGRESS);
        if (gameInProgress.isEmpty()) {
            throw new GameException(NOT_EXIST_IN_PROGRESS);
        }
        return gameInProgress.get(0);
    }
}
