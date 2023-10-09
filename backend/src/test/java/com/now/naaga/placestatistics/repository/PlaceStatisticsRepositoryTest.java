package com.now.naaga.placestatistics.repository;

import com.now.naaga.common.fixture.PositionFixture;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.domain.Place;
import com.now.naaga.placestatistics.domain.PlaceStatistics;
import com.now.naaga.player.domain.Player;
import com.now.naaga.score.domain.Score;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
@DataJpaTest
public class PlaceStatisticsRepositoryTest {

    @Autowired
    private PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    void 동시에_좋아요를_수정하면_예외를_발생한다() {
        final Member member = new Member("aaaa");
        testEntityManager.persist(member);
        final Player player = new Player("dsfs", new Score(1234), member);
        testEntityManager.persist(player);
        final Place place = new Place("1234", null, PositionFixture.서울_좌표, "1234", player);
        testEntityManager.persist(place);
        final PlaceStatistics placeStatistics = new PlaceStatistics(place, 1L);
        testEntityManager.persist(placeStatistics);

        testEntityManager.detach(placeStatistics);

        final PlaceStatistics first = placeStatisticsRepository.findByPlaceId(place.getId())
                .orElseThrow(RuntimeException::new);
        first.subtractLike();

        testEntityManager.detach(first);

        final PlaceStatistics second = placeStatisticsRepository.findByPlaceId(place.getId())
                .orElseThrow(RuntimeException::new);
        second.subtractLike();

        placeStatisticsRepository.save(first);
        assertThrows(OptimisticLockException.class, () -> placeStatisticsRepository.save(second));
    }
}
