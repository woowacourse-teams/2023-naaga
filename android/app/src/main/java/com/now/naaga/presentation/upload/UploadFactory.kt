package com.now.naaga.presentation.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.PlaceRepository

class UploadFactory(
    private val placeRepository: PlaceRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(placeRepository) as T
        } else {
            throw java.lang.IllegalArgumentException()
        }
    }
}
