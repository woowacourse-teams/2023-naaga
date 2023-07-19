package com.now.naaga.config.infrastructure;

import com.now.naaga.auth.infrastructure.BasicAuthenticationDecoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BasicAuthenticationDecoderTest {

    @Test
    void base64로_암호화된_문자열을_해독한다() {
        // given
        BasicAuthenticationDecoder basicAuthenticationDecoder = new BasicAuthenticationDecoder();
        String encoded = "BASIC MTExQHdvb3dhLmNvbToxMTEx";

        // when
        String[] decode = basicAuthenticationDecoder.decode(encoded);

        // then
        Assertions.assertAll(
                () -> assertThat(decode[0]).isEqualTo("111@woowa.com"),
                () -> assertThat(decode[1]).isEqualTo("1111")
        );
    }
}
