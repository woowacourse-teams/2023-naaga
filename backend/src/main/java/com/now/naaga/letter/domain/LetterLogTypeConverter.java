package com.now.naaga.letter.domain;

import com.now.naaga.letter.presentation.LetterLogType;
import org.springframework.core.convert.converter.Converter;

public class LetterLogTypeConverter implements Converter<String, LetterLogType> {

    @Override
    public LetterLogType convert(final String source) {
        return LetterLogType.from(source);
    }
}
