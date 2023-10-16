package com.now.naaga.letter.application;

import com.now.naaga.letter.application.dto.FindLetterByIdCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.exception.LetterExceptionType;
import com.now.naaga.letter.repository.LetterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class LetterFindService {
    
    private final LetterRepository letterRepository;
    
    public LetterFindService(final LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }
    
    public Letter findById(FindLetterByIdCommand findLetterByIdCommand) {
        Long letterId = findLetterByIdCommand.letterId();
        return letterRepository.findById(letterId)
                        .orElseThrow(() -> new LetterException(LetterExceptionType.NOT_EXIST));
    }
}
