package com.now.naaga.letter.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.*;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;
import static com.now.naaga.letter.presentation.LogType.READ;
import static com.now.naaga.letter.presentation.LogType.WRITE;

@RequestMapping("/letters")
@RestController
public class LetterController {

    private final LetterService letterService;

    public LetterController(final LetterService letterService) {
        this.letterService = letterService;
    }

    @PostMapping("/letters")
    public ResponseEntity<LetterResponse> createLetter(@Auth final PlayerRequest playerRequest,
                                                       @RequestBody final LetterRequest letterRequest) {
        final CreateLetterCommand createLetterCommand = CreateLetterCommand.of(playerRequest, letterRequest);
        final Letter letter = letterService.writeLetter(createLetterCommand);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/letters/" + letter.getId()))
                .body(LetterResponse.from(letter));
    }

    @GetMapping("/letters/{letterId}")
    public ResponseEntity<LetterResponse> findLetterById(@Auth final PlayerRequest playerRequest,
                                                         @PathVariable final Long letterId) {
        final LetterReadCommand letterReadCommand = LetterReadCommand.of(playerRequest, letterId);
        final Letter letter = letterService.findLetter(letterReadCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LetterResponse.from(letter));
    }

    @GetMapping("/letters/nearby")
    public ResponseEntity<List<NearByLetterResponse>> findLetterNearBy(@Auth final PlayerRequest playerRequest,
                                                                       @RequestParam final Double latitude,
                                                                       @RequestParam final Double longitude) {
        final FindNearByLetterCommand findNearByLetterCommand = FindNearByLetterCommand.from(latitude, longitude);

        final List<Letter> letters = letterService.findNearByLetters(findNearByLetterCommand);
        final List<NearByLetterResponse> nearByLetterResponses = NearByLetterResponse.convertToLetterResponses(letters);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(nearByLetterResponses);
    }

    @GetMapping("/letterlogs")
    public ResponseEntity<List<LetterResponse>> findLetterInGame(@Auth final PlayerRequest playerRequest,
                                                                 @RequestParam final Long gameId,
                                                                 @RequestParam final String logType) {
        if (!(READ.name().equalsIgnoreCase(logType) || WRITE.name().equalsIgnoreCase(logType))) {
            throw new CommonException(INVALID_REQUEST_PARAMETERS);
        }
        final FindLetterLogByGameCommand findLetterLogByGameCommand = FindLetterLogByGameCommand.of(playerRequest, gameId, logType);
        final List<Letter> letters = letterService.findLetterLogInGame(findLetterLogByGameCommand);

        final List<LetterResponse> writeLetterResponses = letters.stream()
                .map(letter -> LetterResponse.from(letter))
                .toList();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(writeLetterResponses);
    }
}

