package com.now.naaga.player.persistence.repository;

import com.now.naaga.member.domain.Member;
import com.now.naaga.player.domain.Player;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlayerRepository extends JpaRepository<Player, Long> {

    Player findPlayerByMember(Member member);
}
