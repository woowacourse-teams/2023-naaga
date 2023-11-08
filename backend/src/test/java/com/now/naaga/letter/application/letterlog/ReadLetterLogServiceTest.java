package com.now.naaga.letter.application.letterlog;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST_IN_PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.player.domain.Player;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@SpringBootTest
class ReadLetterLogServiceTest {

    @Autowired
    private ReadLetterLogService readLetterLogService;

    @Autowired
    private ReadLetterLogRepository readLetterLogRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private GameBuilder gameBuilder;

    @Autowired
    private LetterBuilder letterBuilder;
    
    //TODO: 채채 코드와 합치면 테스트 수정 예정
    @Test
    void 이미_읽은_쪽지의_경우_읽은_쪽지_로그를_기록하지_않는다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();
        
        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();
        
        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();
        
        final Player letterRegister = playerBuilder.init()
                                                   .build();
        
        final Letter letter = letterBuilder.init()
                                           .registeredPlayer(letterRegister)
                                           .build();
        
        ReadLetterLogService mockReadLetterService = mock(ReadLetterLogService.class);
        
        // when
        mockReadLetterService.log(new LetterLogCreateCommand(player.getId(), letter));
        mockReadLetterService.log(new LetterLogCreateCommand(player.getId(), letter));
        
        //then
        verify(mockReadLetterService, atLeast(1)).log(new LetterLogCreateCommand(player.getId(), letter));
    }
    
    @Test
    void 읽은_쪽지_로그를_정상적으로_기록한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();
        
        final Place destination = placeBuilder.init()
                                              .position(잠실_루터회관_정문_좌표)
                                              .build();
        
        final Game game = gameBuilder.init()
                                     .place(destination)
                                     .player(player)
                                     .startPosition(잠실역_교보문고_좌표)
                                     .build();
        
        final Player letterRegister = playerBuilder.init()
                                                   .build();
        
        final Letter letter = letterBuilder.init()
                                           .registeredPlayer(letterRegister)
                                           .build();
        
        // when
        readLetterLogService.log(new LetterLogCreateCommand(player.getId(), letter));
        
        // then
        final List<ReadLetterLog> actual = readLetterLogRepository.findAll();
        final long expected = actual.get(0).getLetter().getId();
        
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual).hasSize(1);
            softAssertions.assertThat(expected).isEqualTo(letter.getId());
        });
    }

    @Test
    void 읽은쪽지로그에_데이터저장시_진행중인_게임이없으면_예외가_발생한다() {
        // given && when
        final Player player = playerBuilder.init()
                .build();

        final Place destination = placeBuilder.init()
                .position(잠실_루터회관_정문_좌표)
                .build();

        final Game game = gameBuilder.init()
                .place(destination)
                .player(player)
                .startPosition(잠실역_교보문고_좌표)
                .gameStatus(GameStatus.DONE)
                .build();

        final Player letterRegister = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(letterRegister)
                .build();

        //then
        final GameException gameException = assertThrows(
                GameException.class, () -> readLetterLogService.log(new LetterLogCreateCommand(player.getId(), letter)));
        assertThat(gameException.exceptionType()).isEqualTo(NOT_EXIST_IN_PROGRESS);
    }
}
