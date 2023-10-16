package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindGameInProgressCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.application.LetterFindService;
import com.now.naaga.letter.application.dto.FindLetterByIdCommand;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class WriteLetterLogService {
    
    private final WriteLetterLogRepository writeLetterLogRepository;
    
    private final LetterFindService letterFindService;
    
    private final GameService gameService;
    
    public WriteLetterLogService(final WriteLetterLogRepository writeLetterLogRepository,
                                 final LetterFindService letterFindService,
                                 final GameService gameService) {
        this.writeLetterLogRepository = writeLetterLogRepository;
        this.letterFindService = letterFindService;
        this.gameService = gameService;
    }
    
    public void log(final WriteLetterLogCreateCommand writeLetterLogCreateCommand) {
        FindLetterByIdCommand findLetterByIdCommand = new FindLetterByIdCommand(writeLetterLogCreateCommand.letterId());
        Letter letter = letterFindService.findById(findLetterByIdCommand);
        Game gameInProgress = getGameInProgress(letter.getRegisteredPlayer().getId());
        final WriteLetterLog writeLetterLog = new WriteLetterLog(gameInProgress, letter);
        writeLetterLogRepository.save(writeLetterLog);
    }
    
    private Game getGameInProgress(final Long playerId) {
        final FindGameInProgressCommand findGameByStatusCommand = new FindGameInProgressCommand(playerId);
        return gameService.findGameInProgress(findGameByStatusCommand);
    }
    
    public List<WriteLetterLog> findWriteLetterByGameId(final LetterByGameCommand letterByGameCommand) {
        final FindGameByIdCommand findGameByIdCommand = new FindGameByIdCommand(letterByGameCommand.gameId(), letterByGameCommand.playerId());
        final Game game = gameService.findGameById(findGameByIdCommand);
        return writeLetterLogRepository.findByGameId(game.getId());
    }
}
