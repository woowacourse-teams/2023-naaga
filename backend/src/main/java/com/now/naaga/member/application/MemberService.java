package com.now.naaga.member.application;

import static com.now.naaga.member.exception.MemberExceptionType.NOT_EXIST_MEMBER;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.exception.MemberException;
import com.now.naaga.member.exception.MemberExceptionType;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.member.presentation.dto.DeleteMemberCommand;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(final MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public Member findMemberByEmail(final String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
    }

    @Transactional(readOnly = true)
    public Member findMemberById(final Long id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
    }

    public Member create(final CreateMemberCommand createMemberCommand) {
        final Member member = new Member(createMemberCommand.email());
        return memberRepository.save(member);
    }
    
    public void delete(final DeleteMemberCommand deleteMemberCommand) {
        final Long memberId = deleteMemberCommand.memberId();
        final Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(NOT_EXIST_MEMBER));
        // TODO: 8/14/23 멤버 소프트 딜리트 하는 작업 
    }
}
