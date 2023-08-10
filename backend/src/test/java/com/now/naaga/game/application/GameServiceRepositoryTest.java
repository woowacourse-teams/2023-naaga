package com.now.naaga.game.application;

import com.now.naaga.game.domain.ScorePolicy;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.player.application.PlayerService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@Transactional
@SpringBootTest
public class GameServiceRepositoryTest {
        
        @Autowired
        private GameService gameService;
        
        @Autowired
        private ScorePolicy scorePolicy;
        
        @Autowired
        private GameRepository gameRepository;
        
        @Autowired
        private PlayerService playerService;
        @Autowired
        private GameResultRepository gameResultRepository;
        
        @Test
        void 종료된_게임의_결과를_저장한다() {
        
        }
}
