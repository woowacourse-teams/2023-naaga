package com.now.naaga.letter.application;

import com.now.naaga.letter.application.dto.FindNearByLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.repository.LetterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LetterService {

    private final LetterRepository letterRepository;

    public LetterService(final LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), 0.01);
    }
}
