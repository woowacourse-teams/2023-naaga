package com.now.naaga.game.repository;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findByMemberIdAndGameStatus(final Long memberId,
                                           final GameStatus gameStatus);
}
