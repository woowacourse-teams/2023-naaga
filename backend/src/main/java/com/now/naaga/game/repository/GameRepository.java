package com.now.naaga.game.repository;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    
    List<Game> findByPlayerIdAndGameStatus(final Long playerId,
                                           final GameStatus gameStatus);
    
    List<Game> findByPlayerId(Long playerId);
    
    Optional<Game> findByPlayerIdAndInProgress(final Long playerId,
                                               final GameStatus inProgress);
}
