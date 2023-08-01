package com.now.naaga.auth.presentation;

import com.now.naaga.auth.infrastructure.AuthenticationExtractor;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
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
public class AuthenticationArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor;

    private final MemberService memberService;

    private final PlayerService playerService;

    public AuthenticationArgumentResolver(final AuthenticationExtractor<MemberAuthRequest> authenticationExtractor,
                                          final MemberService memberService,
                                          final PlayerService playerService) {
        this.authenticationExtractor = authenticationExtractor;
        this.memberService = memberService;
        this.playerService = playerService;
    }

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.getParameterType().equals(PlayerRequest.class);
    }

    @Override
    public Object resolveArgument(final MethodParameter parameter,
                                  final ModelAndViewContainer mavContainer,
                                  final NativeWebRequest webRequest,
                                  final WebDataBinderFactory binderFactory) throws Exception {

        final HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        final MemberAuthRequest memberAuthRequest = authenticationExtractor.extract(request.getHeader(HttpHeaders.AUTHORIZATION));
        final Member member = memberService.findMemberByEmail(memberAuthRequest.email());
        final Player player = playerService.findPlayerByMemberId(member.getId());
        return new PlayerRequest(player.getId());
    }
}
