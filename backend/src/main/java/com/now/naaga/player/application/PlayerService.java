package com.now.naaga.player.application;

import com.now.naaga.member.application.MemberService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.exception.PlayerExceptionType;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player findPlayerByMemberId(final Long memberId) {
        List<Player> playersByMemberId = playerRepository.findByMemberId(memberId);

        if (playersByMemberId.isEmpty()) {
            throw new PlayerException(PlayerExceptionType.PLAYER_NOT_FOUND);
        }

        return playersByMemberId.get(0);
    }
}
