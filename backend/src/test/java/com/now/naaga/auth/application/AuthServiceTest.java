package com.now.naaga.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.auth.infrastructure.dto.AuthInfo;
import com.now.naaga.auth.infrastructure.dto.MemberAuth;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import java.util.Optional;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
//@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

    @Autowired
    private PlayerBuilder playerBuilder;

    @MockBean
    private AuthClient authClient;

    @Test
    void 존재하지_않는_멤버는_저장_후_토큰을_발급한다() {
        // given
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        final AuthCommand authCommand = new AuthCommand("1234", AuthType.KAKAO);

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(member.getEmail(), player.getNickname()));

        // when
        final AuthToken actual = authService.login(authCommand);
        final Optional<Member> maybeMember = memberRepository.findByEmail(member.getEmail());

        //given
        assertSoftly(softly -> {
            softly.assertThat(maybeMember).isPresent();
            softly.assertThat(actual).isNotNull();
        });
    }

    @Test
    void 존재하는_멤버는_저장_후_토큰을_발급한다() {
        // given
        final Player player = playerBuilder.init()
                .build();
        final AuthCommand authCommand = new AuthCommand("1234", AuthType.KAKAO);

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(player.getMember().getEmail(), player.getNickname()));

        // when
        final AuthToken actual = authService.login(authCommand);

        //given
        assertThat(actual).isNotNull();
    }

    @Test
    void 탈퇴한다() {
        // given
        final Player player = playerBuilder.init()
                .build();
        final long authId = 1L;
        final MemberAuth memberAuth = new MemberAuth(player.getMember().getId(), authId, AuthType.KAKAO);
        doNothing().when(authClient).requestUnlink(authId);
        playerRepository.flush();

        // when
        authService.deleteAccount(memberAuth);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(player.getMember().getId())).isEmpty();
            softly.assertThat(playerRepository.findById(player.getId())).isEmpty();
        });
    }


    @Test
    void 로그아웃한다() {
        // given
        final Player player = playerBuilder.init()
                .build();
        final long authId = 1L;
        final MemberAuth memberAuth = new MemberAuth(player.getMember().getId(), authId, AuthType.KAKAO);
        doNothing().when(authClient).requestUnlink(authId);
        playerRepository.flush();

        // when
        authService.logout(memberAuth);

        // then
        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(memberRepository.findById(player.getMember().getId())).isPresent();
            softly.assertThat(playerRepository.findById(player.getId())).isPresent();
        });
    }
}
