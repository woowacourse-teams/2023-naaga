package com.now.naaga.player.application;

import com.now.naaga.player.domain.Player;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.exception.PlayerExceptionType;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(final Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerException(PlayerExceptionType.PLAYER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByMemberId(final Long memberId) {
        List<Player> playersByMemberId = playerRepository.findByMemberId(memberId);

        if (playersByMemberId.isEmpty()) {
            throw new PlayerException(PlayerExceptionType.PLAYER_NOT_FOUND);
        }

        return playersByMemberId.get(0);
    }
}
