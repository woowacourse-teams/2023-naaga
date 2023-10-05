package com.now.naaga.temporaryplace.application;

import com.now.naaga.temporaryplace.repository.TemporaryPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class TemporaryPlaceService {

    private final TemporaryPlaceRepository temporaryPlaceRepository;

    public TemporaryPlaceService(final TemporaryPlaceRepository temporaryPlaceRepository) {
        this.temporaryPlaceRepository = temporaryPlaceRepository;
    }

    public void deleteById(final Long id) {
        temporaryPlaceRepository.deleteById(id);
    }
}
