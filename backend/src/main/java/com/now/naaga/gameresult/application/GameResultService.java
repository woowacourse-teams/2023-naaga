package com.now.naaga.gameresult.application;

import com.now.naaga.gameresult.persistence.GameResultRepository;
import org.springframework.stereotype.Service;

@Service
public class GameResultService {

    private final GameResultRepository gameResultRepository;

    public GameResultService(final GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }
}
