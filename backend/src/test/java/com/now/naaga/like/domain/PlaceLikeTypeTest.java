package com.now.naaga.like.domain;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class PlaceLikeTypeTest {

    @Test
    void 좋아요_혹은_싫어요_타입을_반대로_바꾼다() {
        // given
        final PlaceLikeType like = PlaceLikeType.LIKE;
        final PlaceLikeType dislike = PlaceLikeType.DISLIKE;

        // when
        final PlaceLikeType actual1 = like.switchType();
        final PlaceLikeType actual2 = dislike.switchType();

        // then
        assertSoftly(softAssertions -> {
            softAssertions.assertThat(actual1).isEqualTo(PlaceLikeType.DISLIKE);
            softAssertions.assertThat(actual2).isEqualTo(PlaceLikeType.LIKE);
        });
    }
}