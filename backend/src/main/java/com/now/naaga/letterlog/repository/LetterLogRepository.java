package com.now.naaga.letterlog.repository;

import com.now.naaga.letterlog.domain.LetterLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LetterLogRepository extends JpaRepository<LetterLog, Long> {
}
