package com.now.naaga.letter.application.letterlog;

import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;
import com.now.naaga.letter.domain.letterlog.WriteLetterLog;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class WriteLetterLogService {

    private final WriteLetterLogRepository writeLetterLogRepository;

    public WriteLetterLogService(final WriteLetterLogRepository writeLetterLogRepository) {
        this.writeLetterLogRepository = writeLetterLogRepository;
    }

    public void log(final WriteLetterLogCreateCommand writeLetterLogCreateCommand) {
        final WriteLetterLog writeLetterLog = new WriteLetterLog(writeLetterLogCreateCommand.game(),
                writeLetterLogCreateCommand.letter());
        writeLetterLogRepository.save(writeLetterLog);
    }
}
