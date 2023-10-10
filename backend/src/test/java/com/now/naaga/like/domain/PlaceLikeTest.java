package com.now.naaga.like.domain;

import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.common.fixture.PlaceLikeFixture;
import com.now.naaga.like.exception.PlaceLikeException;
import com.now.naaga.like.exception.PlaceLikeExceptionType;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PlaceLikeTest {
    
    @Test
    void 좋아요를_누른_당사자가_아니면_예외를_발생한다() {
        // given
        final PlaceLike placeLike = PlaceLikeFixture.PLACE_LIKE();
        final Player newPlayer = new Player(2L, "unknown", new Score(123), new Member("123@naver.com"), false);

        // when & then
        assertSoftly(softAssertions -> {
            final BaseExceptionType baseExceptionType = assertThrows(PlaceLikeException.class, () -> placeLike.validateOwner(newPlayer))
                    .exceptionType();
            assertThat(baseExceptionType).isEqualTo(PlaceLikeExceptionType.INACCESSIBLE_AUTHENTICATION);
        });
    }
}
