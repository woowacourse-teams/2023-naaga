package com.now.naaga.player.presentation;

import com.now.naaga.auth.presentation.annotation.Auth;
import com.now.naaga.player.application.PlayerService;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.player.presentation.dto.PlayerResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/profiles")
@RestController
public class PlayerInfoController {
    
    private final PlayerService playerService;
    
    public PlayerInfoController(final PlayerService playerService) {
        this.playerService = playerService;
    }
    
    @GetMapping("/my")
    public ResponseEntity<PlayerResponse> findMyInfo(@Auth final PlayerRequest playerRequest) {
        final Player player = playerService.findPlayerById(playerRequest.playerId());
        final PlayerResponse playerResponse = PlayerResponse.from(player);
        return ResponseEntity.ok(playerResponse);
    }
    
    
}
