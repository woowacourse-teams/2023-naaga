package com.now.naaga.temporaryplace.presentation.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreateTemporaryPlaceRequest(String name,
                                          String description,
                                          Double latitude,
                                          Double longitude,
                                          MultipartFile imageFile) {
}
