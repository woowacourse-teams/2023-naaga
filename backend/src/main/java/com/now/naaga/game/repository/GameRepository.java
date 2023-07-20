package com.now.naaga.game.repository;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByMemberIdAndGameStatus(Long memberId, GameStatus gameStatus);
}
