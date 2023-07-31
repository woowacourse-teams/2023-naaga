package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MemberService memberService;

    public PlayerService(PlayerRepository playerRepository, MemberService memberService) {
        this.playerRepository = playerRepository;
        this.memberService = memberService;
    }

    public Player findPlayerByMemberId(final Long memberId) {
        final Member member = memberService.findMemberById(memberId);
        return playerRepository.findPlayerByMember(member);
    }
}
