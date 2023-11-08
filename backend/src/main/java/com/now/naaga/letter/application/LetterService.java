package com.now.naaga.letter.application;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameInProgressCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.exception.LetterException;
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
import java.util.Optional;

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

    private void logWriteLetter(final Letter letter) {
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

    private void logReadLetter(final Player player,
                               final Letter letter) {
        final Game gameInProgress = getGameInProgress(player.getId());
        boolean isAlreadyReadLetter = isAlreadyReadLetter(gameInProgress, letter);
        if(!isAlreadyReadLetter) {
            final ReadLetterLog readLetterLog = new ReadLetterLog(gameInProgress, letter);
            readLetterLogRepository.save(readLetterLog);
        }
    }
    private boolean isAlreadyReadLetter(final Game game,
                                        final Letter letter) {
        Optional<ReadLetterLog> readLetterInGame = readLetterLogRepository
                .findByGameIdAndLetterId(game.getId(), letter.getId());
        return readLetterInGame.isPresent();
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), LETTER_RADIUS);
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

    private Game getGameInProgress(final Long playerId) {
        final FindGameInProgressCommand findGameByStatusCommand = new FindGameInProgressCommand(playerId);
        return gameService.findGameInProgress(findGameByStatusCommand);
    }
}

