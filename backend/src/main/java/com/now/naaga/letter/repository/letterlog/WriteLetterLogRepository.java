package com.now.naaga.letter.repository.letterlog;

import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteLetterLogRepository extends JpaRepository<WriteLetterLog, Long> {

    List<WriteLetterLog> findByGameId(Long gamedId);
}
