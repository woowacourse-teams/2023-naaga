package com.now.naaga.place.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.common.exception.BaseExceptionType;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.exception.PlaceException;
import com.now.naaga.place.exception.PlaceExceptionType;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.placestatistics.repository.PlaceStatisticsRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@SpringBootTest
class PlaceServiceTest {

    @Autowired
    private TemporaryPlaceRepository temporaryPlaceRepository;

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private PlaceService placeService;

    @Autowired
    private TemporaryPlaceBuilder temporaryPlaceBuilder;

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

        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long temporaryPlaceId = temporaryPlace.getId();

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

        final TemporaryPlace found = temporaryPlaceRepository.findById(temporaryPlaceId)
                                                             .orElse(null);

        assertSoftly(softAssertions -> {
            assertThat(actual).usingRecursiveComparison()
                              .ignoringExpectedNullFields()
                              .isEqualTo(expected);
            assertThat(found).isNull();
        });
    }

    @Test
    void 장소_등록_시_장소_통계도_함께_등록된다() {
        // given
        final Player player = playerBuilder.init()
                                           .build();

        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long temporaryPlaceId = temporaryPlace.getId();

        final CreatePlaceCommand createPlaceCommand = new CreatePlaceCommand("루터회관",
                                                                             "이곳은 루터회관이다 알겠냐",
                                                                             Position.of(1.23, 4.56),
                                                                             "image/url",
                                                                             player.getId(),
                                                                             temporaryPlaceId);

        // when
        final Place place = placeService.createPlace(createPlaceCommand);

        // then
        final Optional<PlaceStatistics> placeStatistics = placeStatisticsRepository.findByPlaceId(place.getId());
        assertThat(placeStatistics).isNotEmpty();
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
