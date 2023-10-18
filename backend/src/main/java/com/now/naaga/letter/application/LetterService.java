package com.now.naaga.letter.application;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.presentation.LetterLogType;
import com.now.naaga.letter.presentation.dto.FindLetterLogByGameCommand;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.letter.exception.LetterExceptionType.NO_EXIST;

@Transactional
@Service
public class LetterService {

    private static final double LETTER_RADIUS = 0.1;

    private final LetterRepository letterRepository;

    private final ReadLetterLogRepository readLetterLogRepository;

    private final WriteLetterLogRepository writeLetterLogRepository;

    private final PlayerService playerService;

    private final GameService gameService;

    public LetterService(final LetterRepository letterRepository,
                         final ReadLetterLogRepository readLetterLogRepository,
                         final WriteLetterLogRepository writeLetterLogRepository,
                         final PlayerService playerService,
                         final GameService gameService) {
        this.letterRepository = letterRepository;
        this.readLetterLogRepository = readLetterLogRepository;
        this.writeLetterLogRepository = writeLetterLogRepository;
        this.playerService = playerService;
        this.gameService = gameService;
    }

    public Letter writeLetter(final CreateLetterCommand createLetterCommand) {
        final Player player = playerService.findPlayerById(createLetterCommand.playerId());
        final Letter letter = new Letter(player, createLetterCommand.position(), createLetterCommand.message());
        letterRepository.save(letter);

        logWriteLetter(letter);
        return letter;
    }

    public void logWriteLetter(final Letter letter) {
        final Game gameInProgress = getGameInProgress(letter.getRegisteredPlayer().getId());
        final WriteLetterLog writeLetterLog = new WriteLetterLog(gameInProgress, letter);
        writeLetterLogRepository.save(writeLetterLog);
    }

    public Letter findLetter(final LetterReadCommand letterReadCommand) {
        final Player player = playerService.findPlayerById(letterReadCommand.playerId());
        final Letter foundLetter = letterRepository.findById(letterReadCommand.letterId())
                .orElseThrow(() -> new LetterException(NO_EXIST));

        logReadLetter(player, foundLetter);
        return foundLetter;
    }

    public void logReadLetter(final Player player,
                              final Letter letter) {
        final Game gameInProgress = getGameInProgress(player.getId());
        final ReadLetterLog readLetterLog = new ReadLetterLog(gameInProgress, letter);
        readLetterLogRepository.save(readLetterLog);
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), LETTER_RADIUS);
    }

    @Transactional(readOnly = true)
    public List<Letter> findLetterLogInGame(final FindLetterLogByGameCommand findLetterLogByGameCommand) {
        final Game gameInProgress = getGameInProgress(findLetterLogByGameCommand.playerId());
        if (findLetterLogByGameCommand.letterLogType() == LetterLogType.WRITE) {
            final List<WriteLetterLog> writeLetterLogs = writeLetterLogRepository.findByGameId(gameInProgress.getId());
            return writeLetterLogs.stream()
                    .map(WriteLetterLog::getLetter)
                    .toList();
        }
        final List<ReadLetterLog> readLetterLogs = readLetterLogRepository.findByGameId(gameInProgress.getId());
        return readLetterLogs.stream()
                .map(ReadLetterLog::getLetter)
                .toList();
    }

    // 여기 private인데 트렌젝셔이 어떻게 걸리지?
    private Game getGameInProgress(final Long playerId) {
        final FindGameByStatusCommand findGameByStatusCommand = new FindGameByStatusCommand(playerId, IN_PROGRESS);
        final List<Game> gamesInProgress = gameService.findGamesByStatus(findGameByStatusCommand);
        if (gamesInProgress.isEmpty()) {
            throw new GameException(GameExceptionType.NOT_EXIST_IN_PROGRESS);
        }
        return gamesInProgress.get(0);
    }
}

