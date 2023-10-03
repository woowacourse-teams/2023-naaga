package com.now.naaga.temporaryplace.application;

import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
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

    @MockBean
    private TemporaryPlaceRepository temporaryPlaceRepository;

    @Autowired
    private TemporaryPlaceService temporaryPlaceService;

    @Test
    void ID로_검수_장소_데이터를_삭제한다() {
        // given
        doNothing().when(temporaryPlaceRepository).deleteById(anyLong());

        // when
        temporaryPlaceService.deleteById(1L);

        // then
        verify(temporaryPlaceRepository, times(1)).deleteById(1L);
    }
}
