package com.now.naaga.game.application;

import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.GameStatus;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.repository.HintRepository;
import com.now.naaga.place.domain.Place;
import com.now.naaga.place.domain.Position;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class HintService {

    private final HintRepository hintRepository;
    private final GameService gameService;

    public HintService(final HintRepository hintRepository,
                       final GameService gameService) {
        this.hintRepository = hintRepository;
        this.gameService = gameService;
    }

    // TODO: 2023/08/01 힌트 개수에 따라 생성 제약 조건 걸기
    public Hint createHint(final CreateHintCommand command) {
        final Position coordinate = command.coordinate();
        final Game game = gameService.findGame(command.memberId(), command.gameId());
        final Place place = game.getPlace();
        final Direction direction = Direction.calculate(coordinate, place.getPosition());
        final Hint hint = new Hint(coordinate, direction, game);
        return hintRepository.save(hint);
    }
}
