package com.now.naaga.temporaryplace.application;

import static org.assertj.core.api.Assertions.assertThat;

import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class TemporaryPlaceServiceTest {

    @Autowired
    private TemporaryPlaceRepository temporaryPlaceRepository;

    @Autowired
    private TemporaryPlaceService temporaryPlaceService;

    @Autowired
    private TemporaryPlaceBuilder temporaryPlaceBuilder;

    @Test
    void ID로_검수_장소_데이터를_삭제한다() {
        // given
        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long id = temporaryPlace.getId();

        // when
        temporaryPlaceService.deleteById(id);

        // then
        final TemporaryPlace actual = temporaryPlaceRepository.findById(id)
                                                              .orElse(null);

        assertThat(actual).isNull();
    }
}
