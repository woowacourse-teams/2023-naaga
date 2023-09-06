package com.now.naaga.config.infrastructure;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BasicAuthenticationDecoderTest {

//    @Test
//    void base64로_암호화된_문자열을_해독한다() {
//        // given
//        BasicAuthenticationDecoder basicAuthenticationDecoder = new BasicAuthenticationDecoder();
//        String encoded = "BASIC MTExQHdvb3dhLmNvbToxMTEx";
//
//        // when
//        String[] decode = basicAuthenticationDecoder.decode(encoded);
//
//        // then
//        Assertions.assertAll(
//                () -> assertThat(decode[0]).isEqualTo("111@woowa.com"),
//                () -> assertThat(decode[1]).isEqualTo("1111")
//        );
//    }
}
