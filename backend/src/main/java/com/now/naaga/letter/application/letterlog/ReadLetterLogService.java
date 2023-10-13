package com.now.naaga.letter.application.letterlog;

import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ReadLetterLogService {

    private final ReadLetterLogRepository readLetterLogRepository;

    public ReadLetterLogService(final ReadLetterLogRepository readLetterLogRepository) {
        this.readLetterLogRepository = readLetterLogRepository;
    }

    public void log(final LetterLogCreateCommand letterLogCreateCommand) {
        final ReadLetterLog readLetterLog = new ReadLetterLog(letterLogCreateCommand.game(),
                letterLogCreateCommand.letter());
        readLetterLogRepository.save(readLetterLog);
    }
}
