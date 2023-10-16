package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameInProgressCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.application.WriteLetterLogCreateService;
import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class WriteLetterLogService implements WriteLetterLogCreateService {
    
    private final WriteLetterLogRepository writeLetterLogRepository;
    
    private final LetterService letterService;
    
    private final GameService gameService;
    
    public WriteLetterLogService(final WriteLetterLogRepository writeLetterLogRepository,
                                 final LetterService letterService,
                                 final GameService gameService) {
        this.writeLetterLogRepository = writeLetterLogRepository;
        this.letterService = letterService;
        this.gameService = gameService;
    }
    
    @Override
    public void log(final WriteLetterLogCreateCommand writeLetterLogCreateCommand) {
        Game gameInProgress = getGameInProgress(writeLetterLogCreateCommand.letterId());
        Letter letter = letterService.findLetterById(writeLetterLogCreateCommand.letterId());
        final WriteLetterLog writeLetterLog = new WriteLetterLog(gameInProgress, letter);
        writeLetterLogRepository.save(writeLetterLog);
    }
    
    private Game getGameInProgress(final Long playerId) {
        final FindGameInProgressCommand findGameByStatusCommand = new FindGameInProgressCommand(playerId);
        return gameService.findGameInProgress(findGameByStatusCommand);
    }
}
