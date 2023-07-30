package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Players;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
