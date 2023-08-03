package com.now.naaga.player.domain;

import java.util.Objects;

public class Rank {

    private Player player;
    private int rank;
    private int percentage;

    public Rank() {
    }

    public Rank(final Player player,
                final int rank,
                final int percentage) {
        this.player = player;
        this.rank = rank;
        this.percentage = percentage;
    }

    public Player getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public int getPercentage() {
        return percentage;
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
        return rank == rank1.rank && percentage == rank1.percentage && Objects.equals(player, rank1.player);
    }

    @Override
    public int hashCode() {
        return Objects.hash(player, rank, percentage);
    }
}
