package com.now.naaga.letter.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.application.letterlog.dto.LetterCreateCommand;
import com.now.naaga.letter.presentation.dto.LetterRequest;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RequestMapping("/letters")
@RestController
public class LetterController {

    private final LetterService letterService;

    public LetterController(final LetterService letterService) {
        this.letterService = letterService;
    }

    @PostMapping
    public ResponseEntity<LetterResponse> createLetter(@Auth final PlayerRequest playerRequest,
                                                       @RequestBody final LetterRequest letterRequest) {
        final LetterCreateCommand letterCreateCommand = LetterCreateCommand.of(playerRequest, letterRequest);
        final Letter letter = letterService.createLetter(letterCreateCommand);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/letters/" + letter.getId()))
                .body(LetterResponse.of(letter));
    }
}
