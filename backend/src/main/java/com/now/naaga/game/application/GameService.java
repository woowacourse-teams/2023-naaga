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
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlaceService placeService;
    private final MemberService memberService;
    private final PlayerService playerService;

    public GameService(final GameRepository gameRepository,
                       final PlaceService placeService,
                       final MemberService memberService,
                       final PlayerService playerService) {
        this.gameRepository = gameRepository;
        this.placeService = placeService;
        this.memberService = memberService;
        this.playerService = playerService;
    }

    public Game createGame(final MemberCommand memberCommand,
                           final Position position) {
        final List<Game> gamesByStatus = findGamesByStatus(null, GameStatus.IN_PROGRESS.name());
        if (!gamesByStatus.isEmpty()) {
            throw new GameException(ALREADY_IN_PROGRESS);
        }
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Place place = placeService.recommendPlaceByPosition(position);
        final Game game = new Game(new Player(null, null, member), place, position);
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
    public Game findGame(final Long memberId,
                         final Long id) {
        final Member member = memberService.findMemberById(memberId);
        final Game game = gameRepository.findById(id)
                .orElseThrow(() -> new GameException(NOT_EXIST));
        if (!member.equals(game.getPlayer().getMember())) {
            throw new GameException(INACCESSIBLE_AUTHENTICATION);
        }
        return game;
    }

    @Transactional(readOnly = true)
    public List<Game> findGamesByStatus(final Long memberId,
                                        final String gameStatus) {
        final Player player = playerService.findPlayerByMemberId(memberId);
        return gameRepository.findByPlayerIdAndGameStatus(player.getId(), GameStatus.valueOf(gameStatus));
    }
}
