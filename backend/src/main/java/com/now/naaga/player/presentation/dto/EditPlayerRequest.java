package com.now.naaga.player.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record EditPlayerRequest(@NotBlank @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s]+$") String nickname) {
}
