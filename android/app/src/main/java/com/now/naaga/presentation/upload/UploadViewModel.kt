package com.now.naaga.presentation.upload

import android.app.Application
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import com.now.domain.repository.PlaceRepository

class UploadViewModel(
    private val application: Application,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private var imageUri: Uri = Uri.EMPTY

    private val _name = MutableLiveData<String>()
    val title: LiveData<String> = _name

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _coordinate = MutableLiveData<Coordinate>()
    val coordinate: LiveData<Coordinate> = _coordinate

    fun setTitle(editTitle: Editable) {
        _name.value = editTitle.toString()
    }

    fun setDescription(editTitle: Editable) {
        _description.value = editTitle.toString()
    }

    fun setUri(uri: Uri) {
        imageUri = uri
    }

    fun setCoordinate(coordinate: Coordinate) {
        _coordinate.value = coordinate
    }

    fun hasUri(): Boolean {
        return imageUri != Uri.EMPTY
    }

    fun hasCoordinate(): Boolean {
        return _coordinate.value != null
    }

    fun postPlace() {
        _coordinate.value?.let { coordinate ->
            placeRepository.postPlace(
                name = _name.value.toString(),
                description = _description.value.toString(),
                coordinate = coordinate,
                image = getAbsolutePathFromUri(application.applicationContext, imageUri) ?: "",
                callback = { result: Result<Place> ->
                    result
                        .onSuccess { }
                        .onFailure { }
                },
            )
        }
    }

    private fun getAbsolutePathFromUri(context: Context, uri: Uri): String? {
        val projection = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, projection, null, null, null)
        cursor?.use {
            val columnIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (it.moveToFirst()) {
                return it.getString(columnIndex)
            }
        }
        return null
    }
}
