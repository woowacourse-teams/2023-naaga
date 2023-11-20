package com.now.naaga.letter.application;

import com.now.naaga.common.ServiceTest;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.exception.GameExceptionType;
import com.now.naaga.letter.application.dto.CreateLetterCommand;
import com.now.naaga.letter.domain.Letter;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.exception.LetterException;
import com.now.naaga.letter.presentation.dto.FindNearByLetterCommand;
import com.now.naaga.letter.presentation.dto.LetterReadCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.player.domain.Player;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.common.fixture.PositionFixture.*;
import static com.now.naaga.letter.exception.LetterExceptionType.NO_EXIST;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
class LetterServiceTest extends ServiceTest {

    @Autowired
    private LetterService letterService;

//    @Test
//    void 읽은쪽지로그에_데이터저장시_진행중인_게임이없으면_예외가_발생한다() {
//        // given && when
//        final Player player = playerBuilder.init()
//                                           .build();
//
//        final Place destination = placeBuilder.init()
//                                              .position(잠실_루터회관_정문_좌표)
//                                              .build();
//
//        final Game game = gameBuilder.init()
//                                     .place(destination)
//                                     .player(player)
//                                     .startPosition(잠실역_교보문고_좌표)
//                                     .gameStatus(GameStatus.DONE)
//                                     .build();
//
//        final Player letterRegister = playerBuilder.init()
//                                                   .build();
//
//        final Letter letter = letterBuilder.init()
//                                           .registeredPlayer(letterRegister)
//                                           .build();
//
//        //then
//        final GameException gameException = assertThrows(
//                GameException.class, () -> letterService.findLetter(new LetterReadCommand(player.getId(), letter.getId())));
//        assertThat(gameException.exceptionType()).isEqualTo(NOT_EXIST_IN_PROGRESS);
//    }

    @Transactional
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
        final Letter expected = new Letter(savedPlayer, position, message);
        final Letter actual = letterService.writeLetter(createLetterCommand);

        //then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual)
                    .usingRecursiveComparison()
                    .ignoringExpectedNullFields()
                    .isEqualTo(expected);
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

    @Test
    void 플레이어주변_100m_내로의_쪽지만_모두_조회한다() {
        // given
        final Player registerPlayer = playerBuilder.init()
                .build();

        final Letter letter1 = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .build();

        final Letter letter2 = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .position(잠실역_교보문고_110미터_앞_좌표)
                .build();

        // when
        final List<Letter> actual = letterService.findNearByLetters(new FindNearByLetterCommand(잠실역_교보문고_좌표));

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.size()).isEqualTo(1);
            softAssertions.assertThat(actual.get(0).getId()).isEqualTo(letter1.getId());
        });
    }

    @Test
    void 플레이어주변_100m_내로의_쪽지가_없으면_빈리스트를_반환한다() {
        // given
        final Player registerPlayer = playerBuilder.init()
                .build();

        final Letter letter = letterBuilder.init()
                .registeredPlayer(registerPlayer)
                .position(잠실역_교보문고_110미터_앞_좌표)
                .build();

        // when
        final List<Letter> actual = letterService.findNearByLetters(new FindNearByLetterCommand(잠실_루터회관_정문_좌표));

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void 쪽지를_단건조회_한다() {
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
        final Letter actual = letterService.findLetter(new LetterReadCommand(player.getId(), letter.getId()));
        final List<ReadLetterLog> actualLog = readLetterLogRepository.findAll();

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual.getId()).isEqualTo(letter.getId());
            softAssertions.assertThat(actualLog).hasSize(1);
        });
    }

    @Test
    void 쪽지가_존재하지_않으면_예외가_발생한다() {
        // given & when
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

        // then
        final LetterException letterException = assertThrows(
                LetterException.class, () -> letterService.findLetter(new LetterReadCommand(player.getId(), letter.getId() + 1)));
        assertThat(letterException.exceptionType()).isEqualTo(NO_EXIST);
    }
}
