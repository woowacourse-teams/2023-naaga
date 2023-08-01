package com.now.naaga.auth.application;

import static com.now.naaga.auth.exception.AuthExceptionType.PASSWORD_MISMATCH;

import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.presentation.dto.MemberAuthRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class AuthService {

    private final MemberService memberService;

    public AuthService(final MemberService memberService) {
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public void validateAuthentication(final MemberAuthRequest memberAuthRequest) {
        final Member member = memberService.findMemberByEmail(memberAuthRequest.email());
        if (!memberAuthRequest.password().equals(member.getPassword())) {
            throw new AuthException(PASSWORD_MISMATCH);
        }
    }
}
