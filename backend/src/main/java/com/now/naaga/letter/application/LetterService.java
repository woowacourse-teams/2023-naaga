package com.now.naaga.letter.application;

import com.now.naaga.letter.application.letterlog.ReadLetterLogService;
import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
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

    private static final double LETTER_RADIUS = 0.1;

    private final LetterRepository letterRepository;

    private final ReadLetterLogService readLetterLogService;

    private final PlayerService playerService;

    public LetterService(final LetterRepository letterRepository,
                         final ReadLetterLogService readLetterLogService,
                         final PlayerService playerService) {
        this.letterRepository = letterRepository;
        this.readLetterLogService = readLetterLogService;
        this.playerService = playerService;
    }

    public Letter findLetter(final LetterReadCommand letterReadCommand) {
        final Player player = playerService.findPlayerById(letterReadCommand.playerId());
        final Letter foundLetter = letterRepository.findById(letterReadCommand.letterId())
                .orElseThrow(() -> new LetterException(NO_EXIST));

        readLetterLogService.log(new LetterLogCreateCommand(player.getId(), foundLetter));
        return foundLetter;
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), LETTER_RADIUS);
    }
}
