package com.now.naaga.player.persistence.repository;

import com.now.naaga.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    List<Player> findByMemberId(final Long memberId);

    @Override
    @Query("SELECT p FROM Player p JOIN FETCH p.member")
    List<Player> findAll();
}
