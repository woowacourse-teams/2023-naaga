package com.now.naaga.common.presentation.interceptor;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class RequestPatternTest {

    PathMatcher pathMatcher = new AntPathMatcher();

    @ParameterizedTest
    @MethodSource("requestDummy")
    void 매칭되는_요청을_판단한다(final String pathPattern,
                       final HttpMethod matchingMethod,
                       final String requestPath,
                       final String requestMethod,
                       final boolean expected) {
        // given
        final RequestPattern requestPattern = new RequestPattern(pathPattern, matchingMethod);

        // when
        final boolean actual = requestPattern.match(pathMatcher, requestPath, requestMethod);

        // then
        assertThat(actual).isEqualTo(expected);
    }

    static Stream<Arguments> requestDummy() {
        return Stream.of(
                Arguments.arguments("/**", GET, "/places/my", "GET", true),
                Arguments.arguments("/**", POST, "/places/my", "POST", true),
                Arguments.arguments("/**", POST, "/place", "DELETE", false),
                Arguments.arguments("/ranks", GET, "/ranks", "GET", true),
                Arguments.arguments("/ranks/**", GET, "/ranks/my", "GET", true),
                Arguments.arguments("/ranks/**", GET, "/my/ranks", "GET", false)
        );
    }
}
