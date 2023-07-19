package com.now.naaga.game.application;

import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.repository.GameRepository;
import com.now.naaga.member.application.MemberService;
import com.now.naaga.member.domain.Member;
import com.now.naaga.member.dto.MemberCommand;
import com.now.naaga.place.application.PlaceService;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import org.springframework.stereotype.Service;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final PlaceService placeService;
    private final MemberService memberService;

    public GameService(final GameRepository gameRepository, final PlaceService placeService, final MemberService memberService) {
        this.gameRepository = gameRepository;
        this.placeService = placeService;
        this.memberService = memberService;
    }

    public Place recommendPlaceByPosition(final MemberCommand memberCommand, final Position position) {
        final Member member = memberService.findMemberByEmail(memberCommand.getEmail());
        final Long memberId = member.getId();

        Game game = gameRepository.findByMemberIdAndGameStatus(memberId, GameStatus.IN_PROGRESSING)
                .orElseGet(() -> createGame(member, position));

        return game.getPlace();
    }

    private Game createGame(final Member member, final Position position) {
        final Place place = placeService.recommendPlaceByPosition(position);
        final Game game = new Game(member, place);
        return gameRepository.save(game);
    }
}
