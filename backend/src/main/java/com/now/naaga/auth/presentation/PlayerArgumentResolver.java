package com.now.naaga.auth.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class PlayerArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationExtractor<MemberAuth> authenticationExtractor;

    private final PlayerService playerService;

    public PlayerArgumentResolver(final AuthenticationExtractor<MemberAuth> authenticationExtractor,
                                  final PlayerService playerService) {
        this.authenticationExtractor = authenticationExtractor;
        this.playerService = playerService;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Auth.class)
                && parameter.getParameterType().equals(PlayerRequest.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {
        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final MemberAuth memberAuth = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        final Player player = playerService.findPlayerByMemberId(memberAuth.getMemberId());
        return new PlayerRequest(player.getId());
    }
}
