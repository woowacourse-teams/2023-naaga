package com.now.naaga.letter.application;

import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.application.letterlog.WriteLetterLogService;
import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class LetterService {
    
    private final LetterRepository letterRepository;
    
    private final PlayerService playerService;
    
    private final WriteLetterLogService writeLetterLogService;
    
    public LetterService(final LetterRepository letterRepository, final PlayerService playerService, final WriteLetterLogService writeLetterLogService) {
        this.letterRepository = letterRepository;
        this.playerService = playerService;
        this.writeLetterLogService = writeLetterLogService;
    }
    
    public Letter writeLetter(final CreateLetterCommand createLetterCommand) {
        final Player player = playerService.findPlayerById(createLetterCommand.playerId());
        final Letter letter = new Letter(player, createLetterCommand.position(), createLetterCommand.message());
        letterRepository.save(letter);
        
        final WriteLetterLogCreateCommand writeLetterLogCreateCommand = new WriteLetterLogCreateCommand(letter.getId());
        writeLetterLogService.log(writeLetterLogCreateCommand);
        return letter;
    }
}
