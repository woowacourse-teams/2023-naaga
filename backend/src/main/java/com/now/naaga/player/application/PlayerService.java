package com.now.naaga.player.application;

import com.now.naaga.member.domain.Member;
import com.now.naaga.player.application.dto.AddScoreCommand;
import com.now.naaga.player.application.dto.CreatePlayerCommand;
import com.now.naaga.player.application.dto.DeletePlayerCommand;
import com.now.naaga.player.application.dto.EditPlayerNicknameCommand;
import com.now.naaga.player.domain.Player;
import com.now.naaga.player.domain.Rank;
import com.now.naaga.player.exception.PlayerException;
import com.now.naaga.player.persistence.repository.PlayerRepository;
import com.now.naaga.player.presentation.dto.PlayerRequest;
import com.now.naaga.score.domain.Score;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.now.naaga.player.exception.PlayerExceptionType.PLAYER_NOT_FOUND;

@Transactional
@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    public PlayerService(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Player editPlayerNickname(final EditPlayerNicknameCommand editPlayerNicknameCommand) {
        final Long playerId = editPlayerNicknameCommand.playerId();
        final String newNickname = editPlayerNicknameCommand.nickname();
        final Player player = findPlayerById(playerId);
        player.editNickname(newNickname);
        return player;
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
        final String nickname = createPlayerCommand.nickname();
        final Member member = createPlayerCommand.member();
        final Player player = Player.create(nickname, new Score(0), member);
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
