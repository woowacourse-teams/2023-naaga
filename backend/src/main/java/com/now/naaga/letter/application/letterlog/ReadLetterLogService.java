package com.now.naaga.letter.application.letterlog;

import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameByStatusCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ReadLetterLogService {
    
    private final ReadLetterLogRepository readLetterLogRepository;
    
    private final GameService gameService;
    
    public ReadLetterLogService(final ReadLetterLogRepository readLetterLogRepository, final GameService gameService) {
        this.readLetterLogRepository = readLetterLogRepository;
        this.gameService = gameService;
    }
    
    public List<ReadLetterLog> findReadLettersByGameId(final LetterByGameCommand letterByGameCommand) {
        final FindGameByIdCommand findGameByIdCommand = new FindGameByIdCommand(letterByGameCommand.gameId(), letterByGameCommand.playerId());
        final Game game = gameService.findGameById(findGameByIdCommand);
        return readLetterLogRepository.findByGameId(game.getId());
    }
    
    //TODO : 채채 letter 패키지 수정되면 수정할 것1
    public void log(final LetterLogCreateCommand letterLogCreateCommand) {
        final Game gameInProgress = getGameInProgress(letterLogCreateCommand.playerId());
        boolean isAlreadyReadLetter = isAlreadyReadLetter(gameInProgress, letterLogCreateCommand.letter());
        if(!isAlreadyReadLetter) {
            final ReadLetterLog readLetterLog = new ReadLetterLog(gameInProgress, letterLogCreateCommand.letter());
            readLetterLogRepository.save(readLetterLog);
        }
    }
    
    //TODO : 채채 letter 패키지 수정되면 수정할 것2
    private boolean isAlreadyReadLetter(final Game game,
                                     final Letter letter) {
        Optional<ReadLetterLog> readLetterInGame = readLetterLogRepository
                .findByGameIdAndLetterId(game.getId(), letter.getId());
        return readLetterInGame.isPresent();
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
