package com.now.naaga.player.domain;

import java.util.Objects;

public class Rank {

    private Player player;
    private int rank;
    private int topPercent;

    public Rank() {
    }

    public Rank(final Player player,
                final int rank,
                final int topPercent) {
        this.player = player;
        this.rank = rank;
        this.topPercent = topPercent;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public int getTopPercent() {
        return topPercent;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Rank rank1 = (Rank) o;
        return rank == rank1.rank && topPercent == rank1.topPercent && Objects.equals(player, rank1.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, rank, topPercent);
    }
}
