package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.application.dto.FinishGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameRecord;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.game.exception.GameExceptionType.ALREADY_IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;

    private final GameResultRepository gameResultRepository;

    private final PlayerService playerService;

    private final PlaceService placeService;

    public GameService(final GameRepository gameRepository,
                       final GameResultRepository gameResultRepository,
                       final PlayerService playerService,
                       final PlaceService placeService) {
        this.gameRepository = gameRepository;
        this.gameResultRepository = gameResultRepository;
        this.playerService = playerService;
        this.placeService = placeService;
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

    public Game finishGame(final FinishGameCommand finishGameCommand) {
        final Game game = gameRepository.findById(finishGameCommand.gameId())
                .orElseThrow(() -> new GameException(NOT_EXIST));
        final Player player = playerService.findPlayerById(finishGameCommand.playerId());
        game.validateOwner(player);
        game.validateInRange(finishGameCommand.position());
        game.changeGameStatus(GameStatus.DONE);
        return game;
    }

    @Transactional(readOnly = true)
    public Game findGame(final FindGameByIdCommand findGameByIdCommand) {
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
        List<GameResult> gameResultsByGameId = gameResultRepository.findByGameId(gameId);

        if (gameResultsByGameId.isEmpty()) {
            throw new IllegalArgumentException("해당게임의 게임결과가 존재하지 않습니다.");
        }

        return gameResultsByGameId.get(0);
    }

    @Transactional(readOnly = true)
    public GameRecord findGameResult(final Long gameId) {
        final GameResult gameResult = findGameResultByGameId(gameId);
        return GameRecord.from(gameResult);
    }

    @Transactional(readOnly = true)
    public List<GameRecord> findAllGameResult() {
        final List<GameResult> gameResults = gameResultRepository.findAll();
        gameResults.sort((gr1, gr2) -> gr2.getCreatedAt().compareTo(gr1.getCreatedAt()));
        return gameResults.stream()
                .map(GameRecord::from)
                .collect(Collectors.toList());
    }
}
