package com.now.naaga.player.presentation.dto;

public class RankResponse {

    private final Long id;
    private final String nickname;
    private final int totalScore;
    private final int rank;
    private final double topPercent;

    public RankResponse(final Long id,
                        final String nickname,
                        final int totalScore,
                        final int rank,
                        final int topPercent) {
        this.id = id;
        this.nickname = nickname;
        this.totalScore = totalScore;
        this.rank = rank;
        this.topPercent = topPercent;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public int getRank() {
        return rank;
    }

    public double getTopPercent() {
        return topPercent;
    }
}

