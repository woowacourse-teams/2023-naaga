package com.now.naaga.member.service;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(final String email) {
        // TODO: 2023/07/19 costom 예외로 변경
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("멤버 정보가 없습니다."));
    }
}
