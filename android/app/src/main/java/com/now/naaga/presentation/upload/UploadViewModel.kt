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
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable

class UploadViewModel(
    private val application: Application,
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private var imageUri: Uri = Uri.EMPTY
    private var coordinate = DEFAULT_COORDINATE

    private val _name = MutableLiveData<String>()
    val title: LiveData<String> = _name

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _successUpload = MutableLiveData<Boolean>()
    val successUpload: LiveData<Boolean> = _successUpload

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

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
        this.coordinate = coordinate
    }

    fun hasUri(): Boolean {
        return imageUri != Uri.EMPTY
    }

    fun hasCoordinate(): Boolean {
        return coordinate != DEFAULT_COORDINATE
    }

    fun postPlace() {
        placeRepository.postPlace(
            name = _name.value.toString(),
            description = _description.value.toString(),
            coordinate = coordinate,
            image = getAbsolutePathFromUri(application.applicationContext, imageUri) ?: "",
            callback = { result: Result<Place> ->
                result
                    .onSuccess { _successUpload.value = true }
                    .onFailure { setError(it as DataThrowable) }
            },
        )
    }

    private fun setError(throwable: DataThrowable) {
        when (throwable) {
            is UniversalThrowable -> _throwable.value = throwable
            is PlaceThrowable -> _throwable.value = throwable
            else -> {}
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

    companion object {
        val DEFAULT_COORDINATE = Coordinate(-1.0, -1.0)

        const val ALREADY_EXISTS_NEARBY = 505
        const val ERROR_STORE_PHOTO = 215
        const val ERROR_POST_BODY = 205
    }
}
