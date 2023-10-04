package com.now.naaga.place.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.application.TemporaryPlaceService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceServiceTest {

    @MockBean
    private TemporaryPlaceService temporaryPlaceService;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private PlaceBuilder placeBuilder;

    @Autowired
    private PlayerBuilder playerBuilder;

    @Transactional
    @Test
    void 장소를_등록한_뒤_기존의_검수_장소_데이터는_삭제한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final Long temporaryPlaceId = 1L;

        final CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand("루터회관",
                                                                             "이곳은 루터회관이다 알겠냐",
                                                                             Position.of(1.23, 4.56),
                                                                             "image/url",
                                                                             player.getId(),
                                                                             temporaryPlaceId);

        // when
        final Place actual = placeService.createPlace(createPlaceCommand);

        // then
        final Place expected = new Place(createPlaceCommand.name(),
                                         createPlaceCommand.description(),
                                         createPlaceCommand.position(),
                                         createPlaceCommand.imageUrl(),
                                         player);

        assertSoftly(softAssertions -> {
            assertThat(actual).usingRecursiveComparison()
                              .ignoringExpectedNullFields()
                              .isEqualTo(expected);
            verify(temporaryPlaceService, times(1)).deleteById(temporaryPlaceId);
        });
    }

    @Test
    void 장소_등록_시_주변_반경_20M_내에_등록된_장소가_존재한다면_예외가_발생한다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        placeBuilder.init()
                    .position(Position.of(1.234567, 1.234567))
                    .build();

        final CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand("루터회관",
                                                                             "이곳은 루터회관이다 알겠냐",
                                                                             Position.of(1.23456, 1.23456),
                                                                             "image/url",
                                                                             player.getId(),
                                                                             1L);

        // when
        final BaseExceptionType baseExceptionType = assertThrows(PlaceException.class,
                                                                 () -> placeService.createPlace(createPlaceCommand)
                                                                ).exceptionType();

        // then
        assertThat(baseExceptionType).isEqualTo(PlaceExceptionType.ALREADY_EXIST_NEARBY);
    }
}