package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;

@Transactional
@Service
public class ReadLetterLogService {

    private final ReadLetterLogRepository readLetterLogRepository;
    private final GameService gameService;

    public ReadLetterLogService(final ReadLetterLogRepository readLetterLogRepository, final GameService gameService) {
        this.readLetterLogRepository = readLetterLogRepository;
        this.gameService = gameService;
    }

    public List<ReadLetterLog> findReadLetterByGameId(final LetterByGameCommand letterByGameCommand) {
        final FindGameByIdCommand findGameByIdCommand = new FindGameByIdCommand(letterByGameCommand.gameId(), letterByGameCommand.playerId());
        final Game game = gameService.findGameById(findGameByIdCommand);
        final List<ReadLetterLog> readLetterLogs = readLetterLogRepository.findByGameId(game.getId());

        for (final ReadLetterLog readLetterLog : readLetterLogs) {
            readLetterLog.validateOwner(letterByGameCommand.playerId());
        }
        return readLetterLogs;
    }

    public void log(final LetterLogCreateCommand letterLogCreateCommand) {
        final Game gameInProgress = getGameInProgress(letterLogCreateCommand.playerId());
        final ReadLetterLog readLetterLog = new ReadLetterLog(gameInProgress, letterLogCreateCommand.letter());
        readLetterLogRepository.save(readLetterLog);
    }

    private Game getGameInProgress(final Long playerId) {
        final FindGameByStatusCommand findGameByStatusCommand = new FindGameByStatusCommand(playerId, IN_PROGRESS);
        final List<Game> gamesInProgress = gameService.findGamesByStatus(findGameByStatusCommand);
        if (gamesInProgress.isEmpty()) {
            throw new GameException(GameExceptionType.NOT_EXIST_IN_PROGRESS);
        }
        return gamesInProgress.get(0);
    }
}
