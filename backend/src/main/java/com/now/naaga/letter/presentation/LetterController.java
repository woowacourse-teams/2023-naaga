package com.now.naaga.letter.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.presentation.dto.NearByLetterResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RequestMapping("/letters")
@RestController
public class LetterController {

    private final LetterService letterService;

    public LetterController(final LetterService letterService) {
        this.letterService = letterService;
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<NearByLetterResponse>> findLetterNearBy(@Auth final PlayerRequest playerRequest,
                                                                       @RequestParam final Double latitude,
                                                                       @RequestParam final Double longitude) {
        final FindNearByLetterCommand findNearByLetterCommand = FindNearByLetterCommand.from(latitude, longitude);

        final List<Letter> letters = letterService.findNearByLetters(findNearByLetterCommand);
        final List<NearByLetterResponse> nearByLetterResponses = letters.stream()
                .map(NearByLetterResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(nearByLetterResponses);
    }
}
