package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.presentation.dto.PlaceResponse;
import com.now.naaga.member.dto.MemberCommand;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/games")
@RestController
public class GameController {

    private final GameService gameService;

    public GameController(final GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping("/destinations")
    public PlaceResponse findDestination(@Auth MemberCommand memberCommand, @ModelAttribute final Position position) {
        final Place place = gameService.recommendPlaceByPosition(memberCommand, position);
        return PlaceResponse.from(place);
    }
}
