package com.now.naaga.member.persistence.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.member.domain.Member;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@Sql("/truncate.sql")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Transactional
@SpringBootTest
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    void 이메일로_회원을_조회한다() {
        final Member saveMember = memberRepository.save(new Member("1111@woowa.com"));

// when
        final Member foundMember = memberRepository.findByEmailAndDeletedFalse(saveMember.getEmail()).get();

// then
        assertThat(foundMember.getId()).isEqualTo(1L);
    }
}
