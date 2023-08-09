package com.now.naaga.auth.application;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.damain.AuthTokens;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.infrastructure.AuthType;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@Transactional
@SpringBootTest
class AuthServiceTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PlayerRepository playerRepository;

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
        final AuthTokens actual = authService.login(authCommand);
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
        final Member member = new Member("chae@chae.com");
        final Player player = new Player("chae", new Score(0), member);
        playerRepository.save(player);
        final AuthCommand authCommand = new AuthCommand("1234", AuthType.KAKAO);

        when(authClient.requestOauthInfo(any())).thenReturn(AuthInfo.of(member.getEmail(), player.getNickname()));

        // when
        final AuthTokens actual = authService.login(authCommand);

        //given
        assertThat(actual).isNotNull();
    }
}
