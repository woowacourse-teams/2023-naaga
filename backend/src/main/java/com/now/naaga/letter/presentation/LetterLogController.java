package com.now.naaga.letter.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.letter.application.letterlog.ReadLetterLogService;
import com.now.naaga.letter.application.letterlog.WriteLetterLogService;
import com.now.naaga.letter.application.letterlog.dto.LetterByGameCommand;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static com.now.naaga.letter.presentation.LogType.READ;
import static com.now.naaga.letter.presentation.LogType.WRITE;

@RequestMapping("/letterlogs")
@RestController
public class LetterLogController {

    private final ReadLetterLogService readLetterLogService;
    private final WriteLetterLogService writeLetterLogService;

    public LetterLogController(final ReadLetterLogService readLetterLogService,
                               final WriteLetterLogService writeLetterLogService) {
        this.readLetterLogService = readLetterLogService;
        this.writeLetterLogService = writeLetterLogService;
    }

    @GetMapping
    public ResponseEntity<List<LetterResponse>> findLetterInGame(@Auth final PlayerRequest playerRequest,
                                                                 @RequestParam final Long gameId,
                                                                 @RequestParam final String logType) {
        if (!(READ.name().equalsIgnoreCase(logType) || WRITE.name().equalsIgnoreCase(logType))) {
            throw new CommonException(INVALID_REQUEST_PARAMETERS);
        }
        final LetterByGameCommand letterByGameCommand = LetterByGameCommand.of(playerRequest, gameId);

        if (READ.name().equalsIgnoreCase(logType)) {
            return findReadLetterByGameId(letterByGameCommand);
        }
        return findWriteLetterByGameId(letterByGameCommand);
    }

    private ResponseEntity<List<LetterResponse>> findReadLetterByGameId(final LetterByGameCommand letterByGameCommand) {
        final List<ReadLetterLog> readLetterLogs = readLetterLogService.findReadLettersByGameId(letterByGameCommand);
        final List<LetterResponse> readLetterResponses = readLetterLogs.stream()
                .map(readLetterLog -> LetterResponse.from(readLetterLog.getLetter()))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(readLetterResponses);
    }

    private ResponseEntity<List<LetterResponse>> findWriteLetterByGameId(final LetterByGameCommand letterByGameCommand) {
        final List<WriteLetterLog> writeLetterLogs = writeLetterLogService.findWriteLetterByGameId(letterByGameCommand);
        final List<LetterResponse> writeLetterResponses = writeLetterLogs.stream()
                .map(readLetterLog -> LetterResponse.from(readLetterLog.getLetter()))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(writeLetterResponses);
    }
}
