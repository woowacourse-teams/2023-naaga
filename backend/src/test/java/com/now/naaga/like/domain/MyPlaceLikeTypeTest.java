package com.now.naaga.like.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
class MyPlaceLikeTypeTest {

    @ParameterizedTest(name = "PlaceLikeType 이 {0} 일때, MyPlaceLikeType 의 {1} 으로 변환된다.")
    @CsvSource(value = {"LIKE,LIKE", "DISLIKE,DISLIKE"})
    void PlaceLikeType_을_MyPlaceLikeType_으로_변환한다(final String placeLikeTypeValue, final String myPlaceLikeTypeValue) {
        // given
        final PlaceLikeType placeLikeType = PlaceLikeType.valueOf(placeLikeTypeValue);

        // when
        final MyPlaceLikeType actual = MyPlaceLikeType.from(placeLikeType);

        // then
        assertThat(actual.name()).isEqualTo(myPlaceLikeTypeValue);
    }
}
