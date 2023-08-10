package com.now.naaga.game.application;

import static com.now.naaga.game.domain.EndType.ARRIVED;
import static com.now.naaga.game.fixture.MemberFixture.MEMBER_CHAE;
import static com.now.naaga.game.fixture.PlaceFixture.잠실_루터회관;
import static com.now.naaga.game.fixture.PlayerFixture.PLAYER;
import static com.now.naaga.game.fixture.PositionFixture.GS25_방이도곡점_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실_루터회관_정문_근처_좌표;
import static com.now.naaga.game.fixture.PositionFixture.잠실역_교보문고_좌표;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.now.naaga.game.application.dto.CreateGameCommand;
import com.now.naaga.game.application.dto.EndGameCommand;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameResult;
import com.now.naaga.game.domain.ScorePolicy;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.GameResultRepository;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
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
class GameServiceTest {

    @Autowired
    private GameService gameService;
    @Autowired
    private GameRepository gameRepository;
    @Autowired
    private GameResultRepository gameResultRepository;
    @Autowired
    private PlayerService playerService;
    @Autowired
    private PlaceService placeService;
    @Autowired
    private ScorePolicy scorePolicy;

    @Test
    void 제한_횟수_내에_게임_실패_시_예외가_발생하면_시도횟수는_롤백되지_않는다() throws InterruptedException {
        // Given
        Player player = playerService.findPlayerById(1l);
        Game game = gameService.createGame(new CreateGameCommand(Long.valueOf(player.getId()),잠실역_교보문고_좌표));
        Thread.sleep(1000);
        EndGameCommand endGameCommand = new EndGameCommand(player.getId(), ARRIVED, GS25_방이도곡점_좌표, game.getId());
        gameService.endGame(endGameCommand);
    }

}

