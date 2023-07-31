package com.now.naaga.player.presentation.dto;

import com.now.naaga.player.domain.Player;

public class PlayerResponse {

    private Long id;
    private String nickname;
    private int totalScore;

    public PlayerResponse() {
    }

    public PlayerResponse(final Long id,
                          final String nickname,
                          final int totalScore) {
        this.id = id;
        this.nickname = nickname;
        this.totalScore = totalScore;
    }

    public static PlayerResponse to(final Player player) {
        return new PlayerResponse(player.getId(), player.getNickname(), player.getTotalScore().getValue());
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
}

