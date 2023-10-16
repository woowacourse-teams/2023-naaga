package com.now.naaga.letter.application;

import com.now.naaga.letter.application.letterlog.dto.LetterCreateCommand;
import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.naaga.letter.exception.LetterExceptionType.NOT_EXIST;

@Transactional
@Service
public class LetterService {
    
    private final LetterRepository letterRepository;
    
    private final PlayerService playerService;
    
    private final WriteLetterLogCreateService writeLetterLogCreateService;
    
    public LetterService(final LetterRepository letterRepository,
                         final PlayerService playerService,
                         final WriteLetterLogCreateService writeLetterLogCreateService) {
        this.letterRepository = letterRepository;
        this.playerService = playerService;
        this.writeLetterLogCreateService = writeLetterLogCreateService;
    }
    
    @Transactional(readOnly = true)
    public Letter findLetterById(final Long letterId) {
        return letterRepository.findById(letterId)
                               .orElseThrow(() -> new LetterException(NOT_EXIST));
    }
    
    public Letter createLetter(final LetterCreateCommand letterCreateCommand) {
        final Player player = playerService.findPlayerById(letterCreateCommand.playerId());
        final Letter letter = new Letter(player, letterCreateCommand.position(), letterCreateCommand.message());
        final Letter savedLetter = letterRepository.save(letter);
        
        final WriteLetterLogCreateCommand writeLetterLogCreateCommand = new WriteLetterLogCreateCommand(letter.getId());
        writeLetterLogCreateService.log(writeLetterLogCreateCommand);
        return savedLetter;
    }
}
