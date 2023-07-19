package com.now.naaga.auth.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.dto.MemberCommand;
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
            throw new RuntimeException("로그인 실패 - 비밀번호 불일치");
        }
    }
}
