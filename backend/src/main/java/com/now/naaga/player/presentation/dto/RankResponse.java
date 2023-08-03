package com.now.naaga.player.presentation.dto;

import com.now.naaga.player.domain.Rank;

public class RankResponse {

    private PlayerResponse player;
    private int percentage;
    private int rank;

    public RankResponse() {
    }

    public RankResponse(final PlayerResponse player,
                        final int rank,
                        final int topPercent) {
        this.player = player;
        this.rank = rank;
        this.percentage = topPercent;
    }

    public static RankResponse of(final Rank rank) {
        return new RankResponse(PlayerResponse.from(rank.getPlayer()), rank.getRank(), rank.getTopPercent());
    }

    public PlayerResponse getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public int getTopPercent() {
        return percentage;
    }
}

