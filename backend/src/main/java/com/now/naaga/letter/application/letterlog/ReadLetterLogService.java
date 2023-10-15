package com.now.naaga.letter.application.letterlog;

import com.now.naaga.game.application.GameService;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
