package com.now.naaga.letter.presentation.dto;

import jakarta.validation.constraints.NotBlank;

public record LetterRequest(@NotBlank String message,
                            Double latitude,
                            Double longitude) {
}
