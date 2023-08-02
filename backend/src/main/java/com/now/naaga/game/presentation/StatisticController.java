package com.now.naaga.game.presentation;

import com.now.naaga.auth.annotation.Auth;
import com.now.naaga.game.application.GameService;
import com.now.naaga.game.domain.Statistic;
import com.now.naaga.game.presentation.dto.StatisticResponse;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/statistics/my")
@RestController
public class StatisticController {

    private final GameService gameService;

    public StatisticController(final GameService gameService) {
        this.gameService = gameService;
    }

    @GetMapping
    public ResponseEntity<StatisticResponse> findMyStatics(@Auth final PlayerRequest playerRequest) {
        final Statistic statistic = gameService.findStatistic(playerRequest);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(StatisticResponse.from(statistic));
    }
}
