package com.now.naaga.member.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.AbstractTest;
import com.now.naaga.member.domain.Member;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SuppressWarnings("NonAsciiCharacters")
@SpringBootTest
class MemberRepositoryTest extends AbstractTest {

    @Test
    void 이메일로_회원을_조회한다() {
        final Member saveMember = memberRepository.save(new Member("1111@woowa.com"));

// when
        final Member foundMember = memberRepository.findByEmail(saveMember.getEmail()).get();

// then
        assertThat(foundMember.getId()).isEqualTo(1L);
    }
}
