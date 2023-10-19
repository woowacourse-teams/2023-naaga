package com.now.naaga.letter.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.letter.application.LetterService;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.letter.presentation.dto.LetterRequest;
import com.now.naaga.letter.presentation.dto.LetterResponse;
import com.now.naaga.letter.presentation.dto.NearByLetterResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
        final CreateLetterCommand createLetterCommand = CreateLetterCommand.of(playerRequest, letterRequest);
        final Letter letter = letterService.writeLetter(createLetterCommand);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .location(URI.create("/letters/" + letter.getId()))
                .body(LetterResponse.from(letter));
    }
    
    @GetMapping("/{letterId}")
    public ResponseEntity<LetterResponse> findLetterById(@Auth final PlayerRequest playerRequest,
                                                         @PathVariable final Long letterId) {
        final LetterReadCommand letterReadCommand = LetterReadCommand.of(playerRequest, letterId);
        final Letter letter = letterService.findLetter(letterReadCommand);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(LetterResponse.from(letter));
    }
    
    @GetMapping("/nearby")
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
}
