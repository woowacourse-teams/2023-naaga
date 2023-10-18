//package com.now.naaga.letter.application.letterlog;
//
//import com.now.naaga.common.builder.GameBuilder;
//import com.now.naaga.common.builder.LetterBuilder;
//import com.now.naaga.common.builder.PlaceBuilder;
//import com.now.naaga.common.builder.PlayerBuilder;
//import com.now.naaga.game.domain.Game;
//import com.now.naaga.game.domain.GameStatus;
//import com.now.naaga.game.exception.GameException;
//import com.now.naaga.letter.application.letterlog.dto.LetterLogCreateCommand;
//import com.now.naaga.letter.domain.Letter;
//import com.now.naaga.letter.domain.letterlog.ReadLetterLog;
//import com.now.naaga.letter.repository.letterlog.ReadLetterLogRepository;
//import com.now.naaga.place.domain.Place;
//import com.now.naaga.player.domain.Player;
//import org.junit.jupiter.api.DisplayNameGeneration;
//import org.junit.jupiter.api.DisplayNameGenerator;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.jdbc.Sql;
//
//import java.util.List;
//
//import static com.now.naaga.common.fixture.PositionFixture.잠실_루터회관_정문_좌표;
//import static com.now.naaga.common.fixture.PositionFixture.잠실역_교보문고_좌표;
//import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST_IN_PROGRESS;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.assertj.core.api.SoftAssertions.assertSoftly;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@Sql("/truncate.sql")
//@SpringBootTest
//class ReadLetterLogServiceTest {
//
//    @Autowired
//    private ReadLetterLogService readLetterLogService;
//
//    @Autowired
//    private ReadLetterLogRepository readLetterLogRepository;
//
//    @Autowired
//    private PlayerBuilder playerBuilder;
//
//    @Autowired
//    private PlaceBuilder placeBuilder;
//
//    @Autowired
//    private GameBuilder gameBuilder;
//
//    @Autowired
//    private LetterBuilder letterBuilder;
//
//
