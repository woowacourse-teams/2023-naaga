package com.now.naaga.presentation.upload

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.PlaceRepository

class UploadFactory(
    private val application: Application,
    private val placeRepository: PlaceRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UploadViewModel::class.java)) {
            return UploadViewModel(application, placeRepository) as T
        } else {
            throw java.lang.IllegalArgumentException()
        }
    }
}
