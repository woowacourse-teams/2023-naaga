package com.now.naaga.letter.application;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.letterlog.WriteLetterLogService;
import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.LetterCreateCommand;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;

@Transactional
@Service
public class LetterService {

    private final LetterRepository letterRepository;

    private final PlayerService playerService;

    private final GameService gameService;

    private final WriteLetterLogService writeLetterLogService;

    public LetterService(final LetterRepository letterRepository,
                         final PlayerService playerService,
                         final GameService gameService,
                         final WriteLetterLogService writeLetterLogService) {
        this.letterRepository = letterRepository;
        this.playerService = playerService;
        this.gameService = gameService;
        this.writeLetterLogService = writeLetterLogService;
    }

    public Letter createLetter(final LetterCreateCommand letterCreateCommand) {
        final Player player = playerService.findPlayerById(letterCreateCommand.playerId());
        final Letter letter = new Letter(player, letterCreateCommand.position(), letterCreateCommand.message());
        final Letter savedLetter = letterRepository.save(letter);

        final Game gameInProgress = getGameInProgress(player);
        final WriteLetterLogCreateCommand writeLetterLogCreateCommand = new WriteLetterLogCreateCommand(gameInProgress, letter);
        writeLetterLogService.log(writeLetterLogCreateCommand);
        return savedLetter;
    }

    private Game getGameInProgress(final Player player) {
        final FindGameByStatusCommand findGameByStatusCommand = new FindGameByStatusCommand(player.getId(), IN_PROGRESS);
        final List<Game> gamesInProgress = gameService.findGamesByStatus(findGameByStatusCommand);
        if(gamesInProgress.isEmpty()) {
            throw  new GameException(GameExceptionType.NOT_EXIST_IN_PROGRESS);
        }
        return gamesInProgress.get(0);
    }
}
