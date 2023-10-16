package com.now.naaga.letter.application;

import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        final CreateLetterCommand createLetterCommand = new CreateLetterCommand(
                savedPlayer.getId(),
                message,
                position);
        final Letter expected = new Letter(savedPlayer,position, message);
        final Letter actual = letterService.writeLetter(createLetterCommand);
        
        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getMessage()).isEqualTo(message);
            softAssertions.assertThat(actual.getRegisteredPlayer()).isEqualTo(savedPlayer);
        });
    }
    
    @Test
    void 쪽지를_등록할_때_현재_진행_중인_게임이_존재하지_않으면_예외가_발생한다() {
        //given
        final Player savedPlayer = playerBuilder.init()
                                                .build();
        final String message = "날씨가 선선해요.";
        final Position position = 잠실_루터회관_정문_좌표;
        final CreateLetterCommand createLetterCommand = new CreateLetterCommand(
                savedPlayer.getId(),
                message,
                position);
        
        //when&then
        assertThatThrownBy(() -> letterService.writeLetter(createLetterCommand))
                .isInstanceOf(GameException.class);
        Assertions.assertAll(() -> {
            final BaseExceptionType baseExceptionType = assertThrows(GameException.class, () -> letterService.writeLetter(createLetterCommand))
                    .exceptionType();
            assertThat(baseExceptionType).isEqualTo(GameExceptionType.NOT_EXIST_IN_PROGRESS);
        });
    }
}
