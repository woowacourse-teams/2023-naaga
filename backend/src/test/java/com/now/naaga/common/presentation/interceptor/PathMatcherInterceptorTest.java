package com.now.naaga.common.presentation.interceptor;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.Controller;

import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PathMatcherInterceptorTest {

    private final PathMatcher pathMatcher = new AntPathMatcher();

    @ParameterizedTest
    @MethodSource("includeRequestPatternDummy")
    void 특정_패스와_메서드를_인터셉터와_매칭_시킨다_메서드를_입력하지_않으면_모든_메서드를_매칭_시킨다(final String requestPath,
                                                               final String requestMethod,
                                                               final String pathPattern,
                                                               final HttpMethod[] matchingMethods,
                                                               final int count) throws Exception {
        // given
        final HandlerInterceptor handlerInterceptor = Mockito.mock(HandlerInterceptor.class);
        final Controller controller = Mockito.mock(Controller.class);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final PathMatcherInterceptor pathMatcherInterceptor = new PathMatcherInterceptor(handlerInterceptor, pathMatcher);

        // when
        request.setServletPath(requestPath);
        request.setMethod(requestMethod);

        pathMatcherInterceptor.includeRequestPattern(pathPattern, matchingMethods);

        pathMatcherInterceptor.preHandle(request, response, controller);
        // then
        verify(handlerInterceptor, times(count)).preHandle(request, response, controller);
    }

    static Stream<Arguments> includeRequestPatternDummy() {
        return Stream.of(
                Arguments.arguments("/places/my", "POST", "/places/**", new HttpMethod[]{}, 1),
                Arguments.arguments("/places/my", "GET", "/places/**", new HttpMethod[]{}, 1),
                Arguments.arguments("/place", "POST", "/places/**", new HttpMethod[]{}, 0),
                Arguments.arguments("/ranks", "POST", "/ranks", new HttpMethod[]{POST}, 1),
                Arguments.arguments("/ranks", "GET", "/ranks", new HttpMethod[]{POST, GET}, 1),
                Arguments.arguments("/ranks/my", "POST", "/ranks", new HttpMethod[]{POST}, 0)
        );
    }

    @ParameterizedTest
    @MethodSource("excludeRequestPatternDummy")
    void 특정_패스와_메서드를_인터셉터와_매칭_제외_시킨다_메서드를_입력하지_않으면_모든_메서드를_매칭_제외_시킨다(final String requestPath,
                                                               final String requestMethod,
                                                               final String pathPattern,
                                                               final HttpMethod[] matchingMethods,
                                                               final int count) throws Exception {
        // given
        final HandlerInterceptor handlerInterceptor = Mockito.mock(HandlerInterceptor.class);
        final Controller controller = Mockito.mock(Controller.class);
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final PathMatcherInterceptor pathMatcherInterceptor = new PathMatcherInterceptor(handlerInterceptor, pathMatcher);

        // when
        request.setServletPath(requestPath);
        request.setMethod(requestMethod);

        pathMatcherInterceptor.includeRequestPattern("/**")
                .excludeRequestPattern(pathPattern, matchingMethods);

        pathMatcherInterceptor.preHandle(request, response, controller);
        // then
        verify(handlerInterceptor, times(count)).preHandle(request, response, controller);
    }

    static Stream<Arguments> excludeRequestPatternDummy() {
        return Stream.of(
                Arguments.arguments("/places/my", "POST", "/places/**", new HttpMethod[]{}, 0),
                Arguments.arguments("/places/my", "GET", "/places/**", new HttpMethod[]{}, 0),
                Arguments.arguments("/place", "POST", "/places/**", new HttpMethod[]{}, 1),
                Arguments.arguments("/ranks", "POST", "/ranks", new HttpMethod[]{POST}, 0),
                Arguments.arguments("/ranks", "GET", "/ranks", new HttpMethod[]{POST, GET}, 0),
                Arguments.arguments("/ranks/my", "POST", "/ranks", new HttpMethod[]{POST}, 1)
        );
    }
}
