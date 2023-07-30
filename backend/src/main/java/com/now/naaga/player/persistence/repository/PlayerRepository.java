package com.now.naaga.player.persistence.repository;

import com.now.naaga.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findByMemberId(Long memberId);
}
