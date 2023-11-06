package com.now.naaga.letter.application;

import com.now.naaga.letter.application.letterlog.dto.WriteLetterLogCreateCommand;

public interface WriteLetterLogCreateService {
    
    void log(WriteLetterLogCreateCommand writeLetterLogCreateCommand);
}
