package com.now.naaga.place.application.dto;

import org.springframework.web.multipart.MultipartFile;

public record PlaceCommand(String name,
                           String description,
                           Double latitude,
                           Double longitude,
                           MultipartFile imageFile) {
}
