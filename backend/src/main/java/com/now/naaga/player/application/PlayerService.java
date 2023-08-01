package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    public List<Rank> getAllPlayersByRanksAscending() {
        final List<Player> players = playerRepository.findAll();
        players.sort((p1, p2) -> p2.getTotalScore().getValue() - p1.getTotalScore().getValue());

        final List<Rank> ranks = new ArrayList<>();
        for (Player player : players) {
            final int rank = calculateRank(players, player);
            final int percent = calculateTopPercent(players, rank);
            final Rank rankObject = new Rank(player, rank, percent);
            ranks.add(rankObject);
        }

        return ranks;
    }

    @Transactional(readOnly = true)
    public Rank getRankAndTopPercent(final MemberCommand memberCommand) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Player player = playerRepository.findByMemberId(member.getId())
                .orElseThrow(() -> new PlayerException(PLAYER_NOT_FOUND));

        final List<Player> players = playerRepository.findAll();
        players.sort((p1, p2) -> p2.getTotalScore().getValue() - p1.getTotalScore().getValue());

        final int rank = calculateRank(players, player);
        final int percent = calculateTopPercent(players, rank);
        return new Rank(player, rank, percent);
    }

    private int calculateRank(final List<Player> players,
                              final Player player) {
        return players.indexOf(player) + 1;
    }

    private int calculateTopPercent(final List<Player> players,
                                    final int rank) {
        return (int) Math.floor((double) rank / players.size() * 100.0);
    }
}
