package com.now.naaga.letter.application;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.letter.application.letterlog.dto.LetterCreateCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.exception.PlayerException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.now.naaga.common.fixture.MemberFixture.MEMBER;
import static com.now.naaga.common.fixture.PlayerFixture.NICKNAME;
import static com.now.naaga.common.fixture.PlayerFixture.SCORE;
import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ActiveProfiles("test")
@Sql("/truncate.sql")
@SpringBootTest
class LetterServiceTest {
    
    @Autowired
    private LetterService letterService;
    
    @Autowired
    private PlayerBuilder playerBuilder;
    
    @Autowired
    private GameBuilder gameBuilder;
    
    
    @Test
    void 쪽지를_정상적으로_생성하고_게임중_등록한_쪽지를_기록으로_남긴다() {
        //given
        final Player savedPlayer = playerBuilder.init()
                .build();
        final String message = "날씨가 선선해요.";
        final Position position = 잠실_루터회관_정문_좌표;
        gameBuilder.init()
                .player(savedPlayer)
                .build();
        
        //when
        final LetterCreateCommand letterCreateCommand = new LetterCreateCommand(
                savedPlayer.getId(),
                message,
                position);
        final Letter expected = letterService.writeLetter(letterCreateCommand);
        
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(expected.getMessage()).isEqualTo(message);
            softAssertions.assertThat(expected.getRegisteredPlayer()).isEqualTo(savedPlayer);
        });
    }
    
    @Test
    void 쪽지를_등록할_때_현재_진행_중인_게임이_존재하지_않으면_예외가_발생한다() {
        //given
        final Player savedPlayer = playerBuilder.init()
                .build();
        final String message = "날씨가 선선해요.";
        final Position position = 잠실_루터회관_정문_좌표;
        final LetterCreateCommand letterCreateCommand = new LetterCreateCommand(
                savedPlayer.getId(),
                message,
                position);
        
        //when&then
        assertThatThrownBy(() -> letterService.writeLetter(letterCreateCommand))
                .isInstanceOf(GameException.class);
    }
    
    @Test
    void 쪽지를_등록할_때_쪽지를_쓴_플레이어가_존재하지_않으면_예외가_발생한다() {
        //given
        final Player player = new Player(1L,
                NICKNAME,
                SCORE,
                MEMBER(),
                false);
        final String message = "날씨가 선선해요.";
        final Position position = 잠실_루터회관_정문_좌표;
        final LetterCreateCommand letterCreateCommand = new LetterCreateCommand(
                player.getId(),
                message,
                position);
        
        //when&then
        assertThatThrownBy(() -> letterService.writeLetter(letterCreateCommand))
                .isInstanceOf(PlayerException.class);
    }
}
