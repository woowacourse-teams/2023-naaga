package com.now.naaga.game.application;

import static com.now.naaga.game.exception.GameExceptionType.ALREADY_IN_PROGRESS;
import static com.now.naaga.game.exception.GameExceptionType.INACCESSIBLE_AUTHENTICATION;
import static com.now.naaga.game.exception.GameExceptionType.NOT_EXIST;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.exception.GameException;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.application.dto.MemberCommand;
import com.now.naaga.member.domain.Member;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlaceService placeService;
    private final MemberService memberService;

    public GameService(final GameRepository gameRepository,
                       final PlaceService placeService,
                       final MemberService memberService) {
        this.gameRepository = gameRepository;
        this.placeService = placeService;
        this.memberService = memberService;
    }

    public Game createGame(final MemberCommand memberCommand,
                           final Position position) {
        final List<Game> gamesByStatus = findGamesByStatus(memberCommand, GameStatus.IN_PROGRESS.name());
        if (!gamesByStatus.isEmpty()) {
            throw new GameException(ALREADY_IN_PROGRESS);
        }
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Place place = placeService.recommendPlaceByPosition(position);
        final Game game = new Game(member, place);
        return gameRepository.save(game);
    }

    public Game finishGame(final MemberCommand memberCommand,
                           final Position requestPosition,
                           final Long gameId) {
        final Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameException(NOT_EXIST));
        Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        game.validateOwner(member);
        game.validateInRange(requestPosition);
        game.changeGameStatus(GameStatus.DONE);
        return game;
    }

    @Transactional(readOnly = true)
    public Game findGame(final MemberCommand memberCommand,
                         final Long id) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameException(NOT_EXIST));
        if (!member.equals(game.getMember())) {
            throw new GameException(INACCESSIBLE_AUTHENTICATION);
        }
        return game;
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByStatus(final MemberCommand memberCommand,
                                        final String gameStatus) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Long memberId = member.getId();

        return gameRepository.findByMemberIdAndGameStatus(memberId, GameStatus.valueOf(gameStatus));
    }
}
