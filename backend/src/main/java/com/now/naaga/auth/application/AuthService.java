package com.now.naaga.auth.application;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.damain.AuthTokens;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.infrastructure.jwt.JwtGenerator;
import com.now.naaga.member.application.CreateMemberCommand;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthService {

    private final MemberService memberService;

    private final AuthClient authClient;

    private final JwtGenerator jwtGenerator;

    public AuthService(final MemberService memberService,
                       final AuthClient authClient,
                       final JwtGenerator jwtGenerator) {
        this.memberService = memberService;
        this.authClient = authClient;
        this.jwtGenerator = jwtGenerator;
    }

    public AuthTokens login(final AuthCommand authCommand) {
        final AuthInfo kakaoAuthInfo = authClient.requestOauthInfo(authCommand.token());
        final Member member = findOrCreateMember(kakaoAuthInfo);
        return jwtGenerator.generate(member.getId());
    }

    private Member findOrCreateMember(final AuthInfo kakaoAuthInfo) {
        final String email = kakaoAuthInfo.getEmail();
        final CreateMemberCommand createMemberCommand = new CreateMemberCommand(email, "1111");
        try {
            return memberService.findMemberByEmail(email);
        } catch (Exception e) {
            return memberService.create(createMemberCommand);
        }
    }
}
