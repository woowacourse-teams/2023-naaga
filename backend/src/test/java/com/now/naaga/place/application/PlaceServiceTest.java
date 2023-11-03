package com.now.naaga.place.application;

import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.common.infrastructure.AwsS3FileManager;
import com.now.naaga.place.application.dto.CreatePlaceCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.PlaceStatistics;
import com.now.naaga.place.domain.Position;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
import com.now.naaga.player.domain.Player;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.*;

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
        
        assertAll(
                () -> assertThat(actual).usingRecursiveComparison()
                                        .ignoringExpectedNullFields()
                                        .isEqualTo(expected),
                () -> assertThat(found).isNull()
        );
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
        assertAll(
                () -> assertThat(placeStatistics).isNotEmpty()
        );
    }
}
