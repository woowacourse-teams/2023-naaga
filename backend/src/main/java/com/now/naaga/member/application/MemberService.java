package com.now.naaga.member.application;

import static com.now.naaga.member.exception.MemberExceptionType.NOT_EXIST_MEMBER;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.exception.MemberException;
import com.now.naaga.member.persistence.repository.MemberRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true)
    public Member findMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
    }

    // TODO: 8/8/23 Runtime 에러 발생으로 롤백 조건 충족 하여 이상한 서비스 메서드 만듬 수정 필요 
    @Transactional
    public Member findOrCreateMember(final CreateMemberCommand createMemberCommand) {
        return memberRepository.findByEmail(createMemberCommand.email())
                .orElse(createMember(createMemberCommand));
    }
    
    private Member createMember(final CreateMemberCommand createMemberCommand) {
        final Member member = new Member(createMemberCommand.email(), createMemberCommand.password());
        return memberRepository.save(member);
    }
}
