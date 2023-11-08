package com.now.naaga.letter.presentation.converter;

import com.now.naaga.letter.domain.letterlog.LetterLogType;
import org.springframework.core.convert.converter.Converter;

public class LetterLogTypeConverter implements Converter<String, LetterLogType> {

    @Override
    public LetterLogType convert(final String source) {
        return LetterLogType.from(source);
    }
}
