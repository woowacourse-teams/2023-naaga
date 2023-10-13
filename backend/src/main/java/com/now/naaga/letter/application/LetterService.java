package com.now.naaga.letter.application;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.letterlog.ReadLetterLogService;
import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.letter.repository.LetterRepository;
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

    private final LetterRepository letterRepository;
    private final ReadLetterLogService readLetterLogService;
    private final GameService gameService;
    private final PlayerService playerService;

    public LetterService(final LetterRepository letterRepository,
                         final ReadLetterLogService readLetterLogService,
                         final GameService gameService, final PlayerService playerService) {
        this.letterRepository = letterRepository;
        this.readLetterLogService = readLetterLogService;
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @Transactional(readOnly = true)
    public Letter findLetter(final LetterReadCommand letterReadCommand) {
        final Player player = playerService.findPlayerById(letterReadCommand.playerId());
        final Letter foundLetter = findLetterById(letterReadCommand.letterId());
        final Game gameInProgress = getGameInProgress(player);
        final LetterLogCreateCommand letterLogCreateCommand = new LetterLogCreateCommand(gameInProgress, foundLetter);
        readLetterLogService.log(letterLogCreateCommand);
        return foundLetter;
    }

    @Transactional(readOnly = true)
    public Letter findLetterById(final Long id) {
        return letterRepository.findById(id)
                .orElseThrow(() -> new LetterException(NO_EXIST));
    }

    private Game getGameInProgress(final Player player) {
        final FindGameByStatusCommand findGameByStatusCommand = new FindGameByStatusCommand(player.getId(), IN_PROGRESS);
        final List<Game> gamesInProgress = gameService.findGamesByStatus(findGameByStatusCommand);
        if (gamesInProgress.isEmpty()) {
            throw new GameException(GameExceptionType.NOT_EXIST_IN_PROGRESS);
        }
        return gamesInProgress.get(0);
    }
}
