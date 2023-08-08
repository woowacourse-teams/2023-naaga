package com.now.naaga.auth.application;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.damain.AuthTokens;
import com.now.naaga.auth.infrastructure.auth.AuthClient;
import com.now.naaga.auth.infrastructure.jwt.JwtGenerator;
import com.now.naaga.member.application.CreateMemberCommand;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.application.dto.CreatePlayerCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthService {

    private final PlayerService playerService;

    private final MemberService memberService;

    private final AuthClient authClient;

    private final JwtGenerator jwtGenerator;

    public AuthService(final PlayerService playerService,
                       final MemberService memberService,
                       final AuthClient authClient,
                       final JwtGenerator jwtGenerator) {
        this.playerService = playerService;
        this.memberService = memberService;
        this.authClient = authClient;
        this.jwtGenerator = jwtGenerator;
    }

    public AuthTokens login(final AuthCommand authCommand) {
        final AuthInfo authInfo = authClient.requestOauthInfo(authCommand.token());
        final Member member = findOrCreateMember(authInfo);
        return jwtGenerator.generate(member.getId());
    }

    private Member findOrCreateMember(final AuthInfo kakaoAuthInfo) {
        final String email = kakaoAuthInfo.getEmail();
        final CreateMemberCommand createMemberCommand = new CreateMemberCommand(email);
        try {
            return memberService.findMemberByEmail(email);
        } catch (Exception e) {
            return createMemberAndPlayer(kakaoAuthInfo, createMemberCommand);
        }
    }

    private Member createMemberAndPlayer(final AuthInfo kakaoAuthInfo, final CreateMemberCommand createMemberCommand) {
        final Member member = memberService.create(createMemberCommand);
        final CreatePlayerCommand createPlayerCommand = new CreatePlayerCommand(kakaoAuthInfo.getNickname(), member);
        playerService.create(createPlayerCommand);
        return member;
    }
}
