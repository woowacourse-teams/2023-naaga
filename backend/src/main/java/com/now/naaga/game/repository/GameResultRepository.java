package com.now.naaga.game.repository;

import com.now.naaga.game.domain.GameResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByGameId(final Long gameId);
}
