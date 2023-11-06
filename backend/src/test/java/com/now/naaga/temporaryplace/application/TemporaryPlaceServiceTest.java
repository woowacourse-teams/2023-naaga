package com.now.naaga.temporaryplace.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.common.infrastructure.AwsS3FileManager;
import com.now.naaga.temporaryplace.domain.TemporaryPlace;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
    
    @MockBean
    private AwsS3FileManager awsS3FileManager;
    
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
