package com.now.naaga.temporaryplace.presentation;

import com.now.naaga.temporaryplace.application.TemporaryPlaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/temporary-places")
@RestController
public class TemporaryPlaceController {

    private final TemporaryPlaceService temporaryPlaceService;

    public TemporaryPlaceController(final TemporaryPlaceService temporaryPlaceService) {
        this.temporaryPlaceService = temporaryPlaceService;
    }

    @DeleteMapping("/{temporaryPlaceId}")
    public ResponseEntity<Void> deleteTemporaryPlace(@PathVariable final Long temporaryPlaceId) {
        temporaryPlaceService.deleteById(temporaryPlaceId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }
}
