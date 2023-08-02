package com.now.naaga.game.repository;

import com.now.naaga.game.domain.GameResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByGameId(final Long gameId);
}
