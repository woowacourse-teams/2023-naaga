package com.now.naaga.game.application;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.domain.GameStatus.IN_PROGRESS;
import static com.now.naaga.game.fixture.GameFixture.GAME_IN_PROGRESS;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_CHAE;
import static com.now.naaga.game.fixture.PlaceFixture.잠실_루터회관;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.GS25_방이도곡점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_근처_좌표;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.ScorePolicy;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@Transactional
@SpringBootTest
class GameServiceTest {
    
    @Autowired
    private GameService gameService;
    
    @Autowired
    private ScorePolicy scorePolicy;
    
    @MockBean
    private GameRepository gameRepository;
    
    @MockBean
    private PlayerService playerService;
    @MockBean
    private GameResultRepository gameResultRepository;
    
    @Test
    void 종료된_게임의_결과를_저장한다() throws InterruptedException {
        // Given
        EndGameCommand endGameCommand = new EndGameCommand(1l, ARRIVED, 잠실_루터회관_정문_근처_좌표,2l);
        Player player = PLAYER("chae", MEMBER_CHAE());
        Game game = new Game(player, 잠실_루터회관(player), GS25_방이도곡점_좌표);
        
        // Mock 객체 설정
        when(gameRepository.findById(anyLong())).thenReturn(Optional.of(game));
        when(playerService.findPlayerById(anyLong())).thenReturn(player);
        
        // When
        Thread.sleep(1000);
        gameService.endGame(endGameCommand);
        
        // Then
        verify(gameRepository, times(1)).findById(anyLong());
        verify(gameResultRepository, times(1)).save(ArgumentMatchers.any(GameResult.class));
    }
    
}

