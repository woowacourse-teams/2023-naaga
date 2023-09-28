package com.now.naaga.gameresult.repository;

import com.now.naaga.gameresult.domain.GameResult;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {

    List<GameResult> findByGameId(final Long gameId);
}
