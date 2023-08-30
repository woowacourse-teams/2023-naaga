package com.now.naaga.game.persistence;

import com.now.naaga.game.domain.Hint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HintRepository extends JpaRepository<Hint, Long> {

}
