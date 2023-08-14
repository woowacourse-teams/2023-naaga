package com.now.naaga.auth.application;

import com.now.naaga.auth.application.dto.AuthCommand;
import com.now.naaga.auth.application.dto.AuthInfo;
import com.now.naaga.auth.application.dto.RefreshTokenCommand;
import com.now.naaga.auth.domain.AuthToken;
import com.now.naaga.auth.exception.AuthException;
import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.infrastructure.jwt.AuthTokenGenerator;
import com.now.naaga.auth.repository.AuthRepository;
import com.now.naaga.member.application.CreateMemberCommand;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.application.dto.CreatePlayerCommand;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.now.naaga.auth.exception.AuthExceptionType.INVALID_TOKEN;

@Transactional
@Service
public class AuthService {

    private final PlayerService playerService;

    private final MemberService memberService;

    private final AuthClient authClient;

    private final AuthTokenGenerator authTokenGenerator;

    private final AuthRepository authRepository;

    public AuthService(final PlayerService playerService,
                       final MemberService memberService,
                       final AuthClient authClient,
                       final AuthTokenGenerator authTokenGenerator,
                       final AuthRepository authRepository) {
        this.playerService = playerService;
        this.memberService = memberService;
        this.authClient = authClient;
        this.authTokenGenerator = authTokenGenerator;
        this.authRepository = authRepository;
    }

    public AuthToken login(final AuthCommand authCommand) {
        final AuthInfo authInfo = authClient.requestOauthInfo(authCommand.token());
        final Member member = findOrCreateMember(authInfo);
        return authTokenGenerator.generate(member.getId());
    }

    private Member findOrCreateMember(final AuthInfo kakaoAuthInfo) {
        final String email = kakaoAuthInfo.getEmail();
        final CreateMemberCommand createMemberCommand = new CreateMemberCommand(email);
        try {
            return memberService.findMemberByEmail(email);
        } catch (Exception e) {
            return createMemberAndPlayer(kakaoAuthInfo, createMemberCommand);
        }
    }

    private Member createMemberAndPlayer(final AuthInfo kakaoAuthInfo, final CreateMemberCommand createMemberCommand) {
        final Member member = memberService.create(createMemberCommand);
        final CreatePlayerCommand createPlayerCommand = new CreatePlayerCommand(kakaoAuthInfo.getNickname(), member);
        playerService.create(createPlayerCommand);
        return member;
    }

    @Transactional(noRollbackFor = AuthException.class)
    public AuthToken refreshLogin(final RefreshTokenCommand refreshTokenCommand) {
        final String refreshToken = refreshTokenCommand.refreshToken();
        final AuthToken oldAuthToken = authRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new AuthException(INVALID_TOKEN));
        authRepository.delete(oldAuthToken);
        final AuthToken newAuthToken = authTokenGenerator.refresh(oldAuthToken);
        return authRepository.save(newAuthToken);
    }
}
