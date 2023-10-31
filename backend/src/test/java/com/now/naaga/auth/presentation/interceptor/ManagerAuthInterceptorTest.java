package com.now.naaga.auth.presentation.interceptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.now.naaga.auth.exception.AuthException;
import java.util.Base64;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.servlet.mvc.Controller;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class ManagerAuthInterceptorTest {

    @Value("${manager.id}")
    String id;

    @Value("${manager.password}")
    String password;

    @Autowired
    ManagerAuthInterceptor managerAuthInterceptor;

    @Test
    void Auth_헤더를_Base64_헤더를_디코딩해서_관리자_로그인을_처리한다() throws Exception {
        // given
        final String s = id + ":" + password;
        final String authHeader = new String(Base64.getEncoder().encode(s.getBytes()));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final Controller controller = Mockito.mock(Controller.class);
        request.addHeader("Authorization", "Basic " + authHeader);

        // when
        final boolean expected = managerAuthInterceptor.preHandle(request, response, controller);

        // then
        assertThat(expected).isTrue();
    }

    @Test
    void 잘못된_관리자_정보를_입력하면_예외를_발생한다() throws Exception {
        // given
        final String s = "wrongId:wrongPW";
        final String authHeader = new String(Base64.getEncoder().encode(s.getBytes()));
        final MockHttpServletRequest request = new MockHttpServletRequest();
        final MockHttpServletResponse response = new MockHttpServletResponse();
        final Controller controller = Mockito.mock(Controller.class);
        request.addHeader("Authorization", "Basic " + authHeader);

        // when & then
        assertThatThrownBy(() -> managerAuthInterceptor.preHandle(request, response, controller)).isInstanceOf(AuthException.class);
    }
}
