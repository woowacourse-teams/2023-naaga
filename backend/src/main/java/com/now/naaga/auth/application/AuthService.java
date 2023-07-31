package com.now.naaga.auth.application;

import static com.now.naaga.auth.exception.AuthExceptionType.PASSWORD_MISMATCH;

import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
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
    public void validateAuthentication(final MemberCommand memberCommand) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        if (!memberCommand.getPassword().equals(member.getPassword())) {
            throw new AuthException(PASSWORD_MISMATCH);
        }
    }
}
