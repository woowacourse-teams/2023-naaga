package com.now.naaga.gameresult.repository;

import com.now.naaga.gameresult.domain.GameResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByGameId(final Long gameId);

    @Query("SELECT r FROM GameResult r JOIN FETCH r.game LEFT JOIN FETCH r.game.hints Where r.game.player.id = :playerId")
    List<GameResult> findByPlayerId(@Param("playerId") Long playerId);

}
