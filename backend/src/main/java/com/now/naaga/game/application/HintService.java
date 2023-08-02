package com.now.naaga.game.application;

import static com.now.naaga.game.exception.GameExceptionType.HINTS_EXHAUSTED;
import static com.now.naaga.game.exception.GameExceptionType.HINT_NOT_EXIST_IN_GAME;

import com.now.naaga.game.application.dto.CreateHintCommand;
import com.now.naaga.game.application.dto.FindGameByIdCommand;
import com.now.naaga.game.application.dto.FindHintByIdCommand;
import com.now.naaga.game.domain.Direction;
import com.now.naaga.game.domain.Game;
import com.now.naaga.game.domain.Hint;
import com.now.naaga.game.exception.GameException;
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

    public Hint createHint(final CreateHintCommand command) {
        final Game game = gameService.findGame(new FindGameByIdCommand(command.gameId(), command.playerId()));
        if (!game.canUseMoreHint()) {
            throw new GameException(HINTS_EXHAUSTED);
        }
        final Position coordinate = command.coordinate();
        final Place place = game.getPlace();
        final Direction direction = Direction.calculate(coordinate, place.getPosition());
        final Hint hint = new Hint(coordinate, direction, game);
        return hintRepository.save(hint);
    }

    public Hint findHintById(final FindHintByIdCommand command) {
        final Game game = gameService.findGame(new FindGameByIdCommand(command.gameId(), command.playerId()));
        return game.getHints()
                .stream()
                .filter(hint -> hint.getId().equals(command.hintId()))
                .findAny()
                .orElseThrow(() -> new GameException(HINT_NOT_EXIST_IN_GAME));
    }
}
