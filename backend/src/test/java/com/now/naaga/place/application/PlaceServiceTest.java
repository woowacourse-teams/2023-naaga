package com.now.naaga.place.application;

import com.now.naaga.member.fixture.MemberFixture;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.fixture.PlaceFixture;
import com.now.naaga.place.persistence.repository.PlaceRepository;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.fixture.PlayerFixture;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaceServiceTest {

    @InjectMocks
    private PlaceService placeService;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private PlayerService playerService;

    @Test
    void 장소를_저장한다() {
        //when
        when(playerService.findPlayerByMemberCommand(MemberFixture.MEMBER_COMMAND)).thenReturn(PlayerFixture.PLAYER);
        Place acutual = placeService.createPlace(MemberFixture.MEMBER_COMMAND, PlaceFixture.createDummy());
        //then
        assertThat(PlaceFixture.PLACE)
                .usingRecursiveComparison()
                .ignoringFields("id", "imageUrl")
                .isEqualTo(acutual);
    }
}
