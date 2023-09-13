package com.now.naaga.player.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.common.exception.CommonException;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.player.presentation.dto.RankResponse;
import com.now.naaga.score.domain.Score;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static com.now.naaga.common.exception.CommonExceptionType.INVALID_REQUEST_PARAMETERS;

@RequestMapping("/ranks")
@RestController
public class PlayerController {

    private final PlayerService playerService;

    private final MemberRepository memberRepository;
    private final PlayerRepository playerRepository;

    public PlayerController(final PlayerService playerService, final MemberRepository memberRepository, final PlayerRepository playerRepository) {
        this.playerService = playerService;
        this.memberRepository = memberRepository;
        this.playerRepository = playerRepository;
    }

    @GetMapping("/my")
    public ResponseEntity<RankResponse> findMyRank(@Auth final PlayerRequest playerRequest) {
        final Rank rank = playerService.getRankAndTopPercent(playerRequest);
        final RankResponse rankResponse = RankResponse.of(rank);
        return ResponseEntity.ok(rankResponse);
    }

    @GetMapping
    public ResponseEntity<List<RankResponse>> findAllRank(@RequestParam(name = "sort-by") final String sortBy,
                                                          @RequestParam(name = "order") final String order) {
        if (!sortBy.equalsIgnoreCase("RANK") || !order.equalsIgnoreCase("ASCENDING")) {
            System.out.println("sortBy: "+sortBy);
            throw new CommonException(INVALID_REQUEST_PARAMETERS);
        }

        final List<Rank> ranks = playerService.getAllPlayersByRanksAscending();
        final List<RankResponse> rankResponseList = ranks.stream()
                .map(RankResponse::of)
                .collect(Collectors.toList());
        return ResponseEntity.ok(rankResponseList);
    }

    @PostMapping("/save")
    public void insertMember(@RequestParam(name = "email") final String email) {
        final Member member = new Member(email);
        Member member2 = memberRepository.save(member);
        final Player player = new Player("채채",new Score(0),member2);
        playerRepository.save(player);
    }
}
