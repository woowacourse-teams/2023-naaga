package com.now.naaga.temporaryplace.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.now.naaga.common.MySqlContainerServiceTest;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("NonAsciiCharacters")
class TemporaryPlaceServiceTest extends MySqlContainerServiceTest {

    @Autowired
    private TemporaryPlaceService temporaryPlaceService;

    @Test
    void 장소가_생성될_때_ID로_검수_장소_데이터만_삭제된다() {
        // given
        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long id = temporaryPlace.getId();

        // when
        temporaryPlaceService.deleteByIdWhenPlaceCreated(id);

        // then
        final TemporaryPlace actual = temporaryPlaceRepository.findById(id)
                                                              .orElse(null);

        assertAll(
                () -> assertThat(actual).isNull(),
                () -> verify(awsS3FileManager, times(0)).deleteFile(anyString())
                 );
    }

    @Test
    void 검수_장소_등록이_거절될_때_ID로_검수_장소_데이터_삭제_및_S3_이미지_파일이_함께_삭제된다() {
        // given
        final TemporaryPlace temporaryPlace = temporaryPlaceBuilder.init()
                                                                   .build();

        final Long id = temporaryPlace.getId();

        // when
        temporaryPlaceService.deleteByIdWhenTemporaryPlaceDenied(id);

        // then
        final TemporaryPlace actual = temporaryPlaceRepository.findById(id)
                                                              .orElse(null);

        assertAll(
                () -> assertThat(actual).isNull(),
                () -> verify(awsS3FileManager, times(1)).deleteFile(anyString())
                 );
    }
}
