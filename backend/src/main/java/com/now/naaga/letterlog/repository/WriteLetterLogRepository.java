package com.now.naaga.letterlog.repository;

import com.now.naaga.letterlog.domain.WriteLetterLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WriteLetterLogRepository extends JpaRepository<WriteLetterLog, Long> {
}
