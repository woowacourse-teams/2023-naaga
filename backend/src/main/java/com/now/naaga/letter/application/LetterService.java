package com.now.naaga.letter.application;

import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.repository.LetterRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
public class LetterService {

    private static final double LETTER_RADIUS = 0.1;
    private final LetterRepository letterRepository;

    public LetterService(final LetterRepository letterRepository) {
        this.letterRepository = letterRepository;
    }

    @Transactional(readOnly = true)
    public List<Letter> findNearByLetters(final FindNearByLetterCommand findNearByLetterCommand) {
        return letterRepository.findLetterByPositionAndDistance(findNearByLetterCommand.position(), LETTER_RADIUS);
    }
}
