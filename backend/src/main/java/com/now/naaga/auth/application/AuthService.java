package com.now.naaga.auth.application;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.damain.AuthClient;
import com.now.naaga.auth.damain.AuthTokens;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.damain.jwt.JwtGenerator;
import com.now.naaga.member.application.CreateMemberCommand;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.naaga.auth.exception.AuthExceptionType.PASSWORD_MISMATCH;

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

    @Transactional(readOnly = true)
    public void validateAuthentication(final MemberAuthRequest memberAuthRequest) {
        final Member member = memberService.findMemberByEmail(memberAuthRequest.email());
        if (!memberAuthRequest.password().equals(member.getPassword())) {
            throw new AuthException(PASSWORD_MISMATCH);
        }
    }

    public AuthTokens login(final AuthCommand authCommand) {
        final AuthInfo kakaoAuthInfo = authClient.requestOauthInfo(authCommand.token());
        System.out.println(kakaoAuthInfo);
        final CreateMemberCommand createMemberCommand = new CreateMemberCommand(kakaoAuthInfo.getEmail(), "1111");
        final Member member = memberService.findOrCreateMember(createMemberCommand); // 멤버 생성 혹은 찾아오기
        return jwtGenerator.generate(member.getId());
    }
}
