package com.now.naaga.player.application.dto;

import com.now.naaga.member.domain.Member;

public record CreatePlayerCommand(String nickname,
                                  Member member) {
}
