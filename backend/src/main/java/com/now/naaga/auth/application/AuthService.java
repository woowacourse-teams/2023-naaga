package com.now.naaga.auth.application;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.dto.MemberCommand;
import com.now.naaga.member.service.MemberService;
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
        final Member member = findMember(memberCommand);
        if (!memberCommand.getPassword().equals(member.getPassword())) {
            throw new RuntimeException("로그인 실패 - 비밀번호 불일치");
        }
    }

    private Member findMember(final MemberCommand memberCommand) {
        try {
            return memberService.findMemberByEmail(memberCommand.getEmail());
        } catch (RuntimeException e) {
            throw new RuntimeException("로그인 실패 - 이메일 불일치");
        }
    }
}
