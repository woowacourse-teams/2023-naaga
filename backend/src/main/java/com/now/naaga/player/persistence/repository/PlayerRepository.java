package com.now.naaga.player.persistence.repository;

import com.now.naaga.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Optional<Player> findByMemberId(Long memberId);
}
