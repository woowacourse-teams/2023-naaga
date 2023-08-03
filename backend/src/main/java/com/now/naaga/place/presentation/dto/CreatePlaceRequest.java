package com.now.naaga.place.presentation.dto;

import org.springframework.web.multipart.MultipartFile;

public record CreatePlaceRequest(String name,
                                 String description,
                                 Double latitude,
                                 Double longitude,
                                 MultipartFile imageFile) {
}
