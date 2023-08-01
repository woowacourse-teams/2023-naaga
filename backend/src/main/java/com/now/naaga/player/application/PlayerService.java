package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MemberService memberService;

    public PlayerService(PlayerRepository playerRepository, MemberService memberService) {
        this.playerRepository = playerRepository;
        this.memberService = memberService;
    }

    // todo : findPlayerByMemberId(Long memberId)로 바꿀것임!
    // todo : PlayerId라고 명시 하지않고 PLayer만 넣어도 알아서 조회해오지 않나??? 객체로조회>>>?
    public Player findPlayerByMember(MemberCommand memberCommand) {
        Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        return playerRepository.findPlayerByMember(member);
    }
}
