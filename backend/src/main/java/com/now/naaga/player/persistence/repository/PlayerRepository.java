package com.now.naaga.player.persistence.repository;

import com.now.naaga.player.domain.Player;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByMemberId(final Long memberId);
}
