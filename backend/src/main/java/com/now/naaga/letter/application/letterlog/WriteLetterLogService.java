package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class WriteLetterLogService {

    private final WriteLetterLogRepository writeLetterLogRepository;
    private final GameService gameService;

    public WriteLetterLogService(final WriteLetterLogRepository writeLetterLogRepository, final GameService gameService) {
        this.writeLetterLogRepository = writeLetterLogRepository;
        this.gameService = gameService;
    }

    public List<WriteLetterLog> findWriteLetterByGameId(final LetterByGameCommand letterByGameCommand) {
        final FindGameByIdCommand findGameByIdCommand = new FindGameByIdCommand(letterByGameCommand.gameId(), letterByGameCommand.playerId());
        final Game game = gameService.findGameById(findGameByIdCommand);
        final List<WriteLetterLog> writeLetterLogs = writeLetterLogRepository.findByGameId(game.getId());

        for (final WriteLetterLog writeLetterLog : writeLetterLogs) {
            writeLetterLog.validateOwner(letterByGameCommand.playerId());
        }
        return writeLetterLogs;
    }
}
