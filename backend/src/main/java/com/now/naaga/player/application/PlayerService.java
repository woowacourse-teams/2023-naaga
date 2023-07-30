package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Players;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;

@Transactional
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final MemberService memberService;

    public PlayerService(final PlayerRepository playerRepository, final MemberService memberService) {
        this.playerRepository = playerRepository;
        this.memberService = memberService;
    }

    @Transactional(readOnly = true)
    public Players findAllPlayersByRanksAscending() {
        final List<Player> players = playerRepository.findAll();
        players.sort((p1, p2) -> p2.getTotalScore().getValue() - p1.getTotalScore().getValue());

        return new Players(players);
    }

    @Transactional(readOnly = true)
    public Player getRankAndTopPercent(final MemberCommand memberCommand) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());

        playerRepository.findByMemberId(member.getId());
        return playerRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new PlayerException(PLAYER_NOT_FOUND));
    }
}
