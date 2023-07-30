package com.now.naaga.player.domain;

import java.util.List;
import java.util.Objects;

public class Players {

    private final List<Player> players;

    public Players(final List<Player> players) {
        this.players = players;
    }

    public int calculateRank(final Player player) {
        return players.indexOf(player) + 1;
    }

    public double calculateTopPercent(final int rank) {
        return (double) (players.size() - rank) / players.size() * 100.0;
    }

    public List<Player> getPlayers() {
        return players;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Players players1 = (Players) o;
        return Objects.equals(players, players1.players);
    }

    @Override
    public int hashCode() {
        return Objects.hash(players);
    }
}
