package com.now.naaga.common;

import com.now.naaga.auth.infrastructure.AuthClient;
import com.now.naaga.auth.persistence.AuthRepository;
import com.now.naaga.common.builder.GameBuilder;
import com.now.naaga.common.builder.GameResultBuilder;
import com.now.naaga.common.builder.LetterBuilder;
import com.now.naaga.common.builder.MemberBuilder;
import com.now.naaga.common.builder.PlaceBuilder;
import com.now.naaga.common.builder.PlaceLikeBuilder;
import com.now.naaga.common.builder.PlaceStatisticsBuilder;
import com.now.naaga.common.builder.PlayerBuilder;
import com.now.naaga.common.builder.ReadLetterLogBuilder;
import com.now.naaga.common.builder.TemporaryPlaceBuilder;
import com.now.naaga.common.builder.WriteLetterLogBuilder;
import com.now.naaga.common.infrastructure.AwsS3FileManager;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.game.repository.HintRepository;
import com.now.naaga.gameresult.repository.GameResultRepository;
import com.now.naaga.letter.repository.LetterRepository;
import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
import com.now.naaga.letter.repository.letterlog.WriteLetterLogRepository;
import com.now.naaga.like.repository.PlaceLikeRepository;
import com.now.naaga.member.persistence.repository.MemberRepository;
import com.now.naaga.place.repository.PlaceRepository;
import com.now.naaga.place.repository.PlaceStatisticsRepository;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

@DisplayNameGeneration(ReplaceUnderscores.class)
@Sql("/truncate.sql")
@ActiveProfiles("test")
public abstract class AbstractTest {

    /*------------------------------------------------------------------*/

    @Autowired
    protected MemberBuilder memberBuilder;

    @Autowired
    protected PlayerBuilder playerBuilder;

    @Autowired
    protected PlaceBuilder placeBuilder;

    @Autowired
    protected GameBuilder gameBuilder;

    @Autowired
    protected GameResultBuilder gameResultBuilder;

    @Autowired
    protected PlaceStatisticsBuilder placeStatisticsBuilder;

    @Autowired
    protected TemporaryPlaceBuilder temporaryPlaceBuilder;

    @Autowired
    protected PlaceLikeBuilder placeLikeBuilder;

    @Autowired
    protected LetterBuilder letterBuilder;

    @Autowired
    protected ReadLetterLogBuilder readLetterLogBuilder;

    @Autowired
    protected WriteLetterLogBuilder writeLetterLogBuilder;

    /*------------------------------------------------------------------*/

    @Autowired
    protected AuthRepository authRepository;

    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected PlayerRepository playerRepository;

    @Autowired
    protected PlaceRepository placeRepository;

    @Autowired
    protected GameRepository gameRepository;

    @Autowired
    protected HintRepository hintRepository;

    @Autowired
    protected GameResultRepository gameResultRepository;

    @Autowired
    protected PlaceStatisticsRepository placeStatisticsRepository;

    @Autowired
    protected TemporaryPlaceRepository temporaryPlaceRepository;

    @Autowired
    protected PlaceLikeRepository placeLikeRepository;

    @Autowired
    protected LetterRepository letterRepository;

    @Autowired
    protected ReadLetterLogRepository readLetterLogRepository;

    @Autowired
    protected WriteLetterLogRepository writeLetterLogRepository;

    /*------------------------------------------------------------------*/

    @MockBean
    protected AuthClient authClient;

    @MockBean
    protected AwsS3FileManager awsS3FileManager;
}
