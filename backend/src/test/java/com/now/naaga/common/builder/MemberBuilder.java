package com.now.naaga.common.builder;

import static com.now.naaga.common.fixture.MemberFixture.MEMBER_EMAIL;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MemberBuilder {

    @Autowired
    private MemberRepository memberRepository;

    private String email;

    public MemberBuilder init() {
        this.email = MEMBER_EMAIL;
        return this;
    }

    public MemberBuilder email(final String email) {
        this.email = email;
        return this;
    }

    public Member build() {
        final Member member = new Member(email);
        return memberRepository.save(member);
    }
}
