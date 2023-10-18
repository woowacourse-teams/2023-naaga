package com.now.naaga.letter.presentation.dto;

public record LetterRequest(String message,
                            Double latitude,
                            Double longitude) {
}
