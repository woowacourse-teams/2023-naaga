package com.now.naaga.player.application;

import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;

import com.now.naaga.player.application.dto.AddScoreCommand;
import com.now.naaga.player.application.dto.CreatePlayerCommand;
import com.now.naaga.player.application.dto.DeletePlayerCommand;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.score.domain.Score;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public Player findPlayerById(final Long id) {
        return playerRepository.findById(id)
                .orElseThrow(() -> new PlayerException(PLAYER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Player findPlayerByMemberId(final Long memberId) {
        List<Player> playersByMemberId = playerRepository.findByMemberId(memberId);

        if (playersByMemberId.isEmpty()) {
            throw new PlayerException(PLAYER_NOT_FOUND);
        }

        return playersByMemberId.get(0);
    }

    @Transactional(readOnly = true)
    public List<Rank> getAllPlayersByRanksAscending() {
        final List<Player> players = playerRepository.findAll();
        players.sort((p1, p2) -> p2.getTotalScore().getValue() - p1.getTotalScore().getValue());

        final List<Rank> ranks = new ArrayList<>();
        for (Player player : players) {
            final int rank = calculateRank(players, player);
            final int percent = calculateTopPercent(players, rank);
            final Rank rankObject = new Rank(player, rank, percent);
            ranks.add(rankObject);
        }

        return ranks;
    }

    @Transactional(readOnly = true)
    public Rank getRankAndTopPercent(final PlayerRequest playerRequest) {
        final Player player = findPlayerById(playerRequest.playerId());

        final List<Player> players = playerRepository.findAll();
        players.sort((p1, p2) -> p2.getTotalScore().getValue() - p1.getTotalScore().getValue());

        final int rank = calculateRank(players, player);
        final int percent = calculateTopPercent(players, rank);
        return new Rank(player, rank, percent);
    }

    private int calculateRank(final List<Player> players,
                              final Player player) {
        return players.indexOf(player) + 1;
    }

    private int calculateTopPercent(final List<Player> players,
                                    final int rank) {
        return (int) Math.floor((double) rank / players.size() * 100.0);
    }

    public Player create(final CreatePlayerCommand createPlayerCommand) {
        final Player player = new Player(createPlayerCommand.nickname(), new Score(0), createPlayerCommand.member());
        return playerRepository.save(player);
    }

    public void addScore(final AddScoreCommand addScoreCommand) {
        final Long playerId = addScoreCommand.playerId();
        final Score score = addScoreCommand.score();
        final Player player = findPlayerById(playerId);

        player.addScore(score);
    }

    public void deleteByMemberId(final DeletePlayerCommand deletePlayerCommand) {
        final List<Player> players = playerRepository.findByMemberId(deletePlayerCommand.memberId());
        playerRepository.deleteAll(players);
    }
}
