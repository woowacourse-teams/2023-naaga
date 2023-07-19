package com.now.naaga.game.repository;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

    Optional<Game> findByMemberIdAndGameStatus(Long memberId, GameStatus gameStatus);
}
