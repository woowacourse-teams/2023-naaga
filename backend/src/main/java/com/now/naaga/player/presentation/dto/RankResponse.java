package com.now.naaga.player.presentation.dto;

import com.now.naaga.player.domain.Player;

public class RankResponse {

    private PlayerResponse player;
    private int rank;
    private int topPercent;

    public RankResponse() {
    }

    public RankResponse(final PlayerResponse player,
                        final int rank,
                        final int topPercent) {
        this.player = player;
        this.rank = rank;
        this.topPercent = topPercent;
    }

    public static RankResponse to(final Player player,
                                  final int rank,
                                  final int topPercent) {
        return new RankResponse(PlayerResponse.to(player), rank, topPercent);
    }

    public PlayerResponse getPlayer() {
        return player;
    }

    public int getRank() {
        return rank;
    }

    public int getTopPercent() {
        return topPercent;
    }
}

