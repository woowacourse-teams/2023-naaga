package com.now.naaga.member.persistence.repository;

import com.now.naaga.member.domain.Member;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 이메일로_회원을_조회한다() {
        // when
        Member foundMember = memberRepository.findByEmail("111@woowa.com").get();

        // then
        assertThat(foundMember.getId()).isEqualTo(1L);
    }
}
