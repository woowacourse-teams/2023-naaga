package com.now.naaga.letter.repository.letterlog;

import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReadLetterLogRepository extends JpaRepository<ReadLetterLog, Long> {

    List<ReadLetterLog> findByGameId(Long gamedId);
}
