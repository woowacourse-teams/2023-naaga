package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameInProgressCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.presentation.dto.FindLetterLogByGameCommand;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import com.now.naaga.player.domain.Player;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
@Service
public class LetterLogService {

    private final ReadLetterLogRepository readLetterLogRepository;

    private final WriteLetterLogRepository writeLetterLogRepository;

    private final GameService gameService;

    public LetterLogService(final ReadLetterLogRepository readLetterLogRepository,
                            final WriteLetterLogRepository writeLetterLogRepository,
                            final GameService gameService) {
        this.readLetterLogRepository = readLetterLogRepository;
        this.writeLetterLogRepository = writeLetterLogRepository;
        this.gameService = gameService;
    }

    public void logWriteLetter(final Letter letter) {
        final Game gameInProgress = getGameInProgress(letter.getRegisteredPlayer().getId());
        final WriteLetterLog writeLetterLog = new WriteLetterLog(gameInProgress, letter);
        writeLetterLogRepository.save(writeLetterLog);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Async
    public void logReadLetter(final Player player,
                              final Letter letter) {
        final FindGameInProgressCommand findGameByStatusCommand = new FindGameInProgressCommand(player.getId());
        final Game gameInProgress = gameService.findGameInProgress(findGameByStatusCommand);
        if (!isAlreadyReadLetter(gameInProgress.getId(), letter.getId())) {
            final ReadLetterLog readLetterLog = new ReadLetterLog(gameInProgress, letter);
            readLetterLogRepository.save(readLetterLog);
        }
    }

    private boolean isAlreadyReadLetter(final Long gameId,
                                        final Long letterId) {
        final Optional<ReadLetterLog> readLetterInGame = readLetterLogRepository
                .findByGameIdAndLetterId(gameId, letterId);
        return readLetterInGame.isPresent();
    }

    private Game getGameInProgress(final Long playerId) {
        final FindGameInProgressCommand findGameByStatusCommand = new FindGameInProgressCommand(playerId);
        final Game gameInProgress = gameService.findGameInProgress(findGameByStatusCommand);
        return gameInProgress;
    }

    @Transactional(readOnly = true)
    public List<Letter> findLetterLogInGame(final FindLetterLogByGameCommand findLetterLogByGameCommand) {
        if (findLetterLogByGameCommand.letterLogType().isWrite()) {
            final List<WriteLetterLog> writeLetterLogs = writeLetterLogRepository.findByGameId(findLetterLogByGameCommand.gameId());
            return writeLetterLogs.stream()
                    .map(WriteLetterLog::getLetter)
                    .toList();
        }
        final List<ReadLetterLog> readLetterLogs = readLetterLogRepository.findByGameId(findLetterLogByGameCommand.gameId());
        return readLetterLogs.stream()
                .map(ReadLetterLog::getLetter)
                .toList();
    }
}
