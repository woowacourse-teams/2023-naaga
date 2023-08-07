package com.now.naaga.common.config;

import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;

@Service
public class OAuthLoginService {

    private final PlayerRepository playerRepository;
    private final MemberRepository memberRepository;
    private final AuthTokensGenerator authTokensGenerator;
    private final RequestOAuthInfoService requestOAuthInfoService;

    public OAuthLoginService(final PlayerRepository playerRepository,
                             final MemberRepository memberRepository,
                             final AuthTokensGenerator authTokensGenerator,
                             final RequestOAuthInfoService requestOAuthInfoService) {
        this.playerRepository = playerRepository;
        this.memberRepository = memberRepository;
        this.authTokensGenerator = authTokensGenerator;
        this.requestOAuthInfoService = requestOAuthInfoService;
    }

    public AuthTokens login(final KakaoLoginParams params) {
        KakaoInfoResponse kakaoInfoResponse = requestOAuthInfoService.request(params);
        Long memberId = findOrCreateMember(kakaoInfoResponse);
        return authTokensGenerator.generate(memberId);
    }

    private Long findOrCreateMember(final KakaoInfoResponse kakaoInfoResponse) {
        return memberRepository.findByEmail(kakaoInfoResponse.getEmail())
                .map(Member::getId)
                .orElseGet(() -> newMember(kakaoInfoResponse));
    }

    private Long newMember(final KakaoInfoResponse kakaoInfoResponse) {
        final Member member = new Member(kakaoInfoResponse.getEmail(), "1111");
        memberRepository.save(member);

        final Player player = new Player(kakaoInfoResponse.getNickname(), new Score(0), member);
        playerRepository.save(player);

        return member.getId();
    }
}
