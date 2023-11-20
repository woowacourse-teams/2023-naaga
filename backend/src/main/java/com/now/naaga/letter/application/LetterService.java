package com.now.naaga.letter.application;

import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.application.letterlog.LetterLogService;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.letter.exception.LetterExceptionType.NO_EXIST;

@Transactional
@Service
public class LetterService {

    private static final double LETTER_RADIUS = 0.05;

    private final LetterRepository letterRepository;

    private final PlayerService playerService;

    private final LetterLogService letterLogService;

    public LetterService(final LetterRepository letterRepository,
                         final PlayerService playerService,
                         final LetterLogService letterLogService) {
        this.letterRepository = letterRepository;
        this.playerService = playerService;
        this.letterLogService = letterLogService;
    }

    public Letter writeLetter(final CreateLetterCommand createLetterCommand) {
        final Player player = playerService.findPlayerById(createLetterCommand.playerId());
        final Letter letter = new Letter(player, createLetterCommand.position(), createLetterCommand.message());
        letterRepository.save(letter);

        letterLogService.logWriteLetter(letter);
        return letter;
    }

    @Transactional(readOnly = true)
    public Letter findLetter(final LetterReadCommand letterReadCommand) {
        final Player player = playerService.findPlayerById(letterReadCommand.playerId());
        final Letter foundLetter = letterRepository.findById(letterReadCommand.letterId())
                .orElseThrow(() -> new LetterException(NO_EXIST));

        letterLogService.logReadLetter(player, foundLetter);
        return foundLetter;
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), LETTER_RADIUS);
    }
}

