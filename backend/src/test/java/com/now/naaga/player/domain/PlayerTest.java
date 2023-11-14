package com.now.naaga.player.domain;

import com.now.naaga.common.fixture.MemberFixture;
import com.now.naaga.common.fixture.PlayerFixture;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.score.domain.Score;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlayerTest {

    @MethodSource("unavailableNicknames")
    @ParameterizedTest
    void 플레이어_생성_시_닉네임에_한글_영어_숫자_공백_이외의_문자를_입력하거나_문자가_2자부터_20자에서_벗어나면_예외를_발생한다(final String nickname) {
        // given
        final Member member = MemberFixture.MEMBER();
        final Score totalScore = new Score(100);

        // when & then
        assertThatThrownBy(() -> new Player(nickname, totalScore, member)).isInstanceOf(PlayerException.class);
    }

    @MethodSource("availableNicknames")
    @ParameterizedTest
    void 플레이어를_생성한다(final String nickname) {
        // given
        final Member member = MemberFixture.MEMBER();
        final Score totalScore = new Score(100);

        // when
        final Player player = new Player(nickname, totalScore, member);

        // then
        assertThat(player).isNotNull();
    }

    @MethodSource("unavailableNicknames")
    @ParameterizedTest
    void 플레이어_닉네임_변경_시_닉네임에_한글_영어_숫자_공백_이외의_문자를_입력하거나_문자가_2자부터_20자에서_벗어나면_예외를_발생한다(final String nickname) {
        // given
        final Player player = PlayerFixture.PLAYER();

        // when & then
        assertThatThrownBy(() -> player.editNickname(nickname)).isInstanceOf(PlayerException.class);
    }

    @MethodSource("availableNicknames")
    @ParameterizedTest
    void 플레이어_닉네임을_변경한다(final String nickname) {
        // given
        final Player player = PlayerFixture.PLAYER();

        // when
        player.editNickname(nickname);

        // then
        assertThat(player.getNickname()).isEqualTo(nickname);
    }

    @Test
    void 잘못된_문자로_닉네임_입력이_들어오면_잘못된_문자_지우고_20글자로_잘라서_플레이어를_생성한다() {
        // given
        final String input = "루!#@!#@카".repeat(100);
        final String expected ="루카".repeat(10);

        // when
        final Player player = Player.create(input, new Score(0), MemberFixture.MEMBER());

        // then
        assertThat(player.getNickname()).isEqualTo(expected);
    }

    static Stream<String> unavailableNicknames() {
        return Stream.of(
                "특수문자#$%@닉네임",
                "10글자가 넘는 닉네임입니다. 이것은 안됩니다.12345678",
                "1"
        );
    }

    static Stream<String> availableNicknames() {
        return Stream.of(
                "가능한 닉네임",
                "루카20살",
                "hello"
        );
    }
}
