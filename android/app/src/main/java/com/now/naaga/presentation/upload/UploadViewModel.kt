package com.now.naaga.presentation.upload

import android.net.Uri
import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.model.Coordinate
import com.now.domain.repository.PlaceRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.PlaceThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import com.now.naaga.util.MutableSingleLiveData
import com.now.naaga.util.SingleLiveData
import kotlinx.coroutines.launch

class UploadViewModel(
    private val placeRepository: PlaceRepository,
) : ViewModel() {
    private var imageUri: String = URI_EMPTY

    private val _name = MutableLiveData<String>()
    val title: LiveData<String> = _name

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _successUpload = MutableSingleLiveData<UploadStatus>()
    val successUpload: SingleLiveData<UploadStatus> = _successUpload

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    private val _coordinate = MutableLiveData<Coordinate>()
    val coordinate: LiveData<Coordinate> = _coordinate

    fun setTitle(editTitle: Editable) {
        _name.value = editTitle.toString()
    }

    fun setDescription(editTitle: Editable) {
        _description.value = editTitle.toString()
    }

    fun setUri(uri: String) {
        imageUri = uri
    }

    fun setCoordinate(coordinate: Coordinate) {
        _coordinate.value = coordinate
    }

    fun hasUri(): Boolean {
        return imageUri != URI_EMPTY
    }

    fun hasCoordinate(): Boolean {
        return _coordinate.value != null
    }

    fun postPlace() {
        _coordinate.value?.let { coordinate ->
            _successUpload.setValue(UploadStatus.PENDING)
            viewModelScope.launch {
                runCatching {
                    placeRepository.postPlace(
                        name = _name.value.toString(),
                        description = _description.value.toString(),
                        coordinate = coordinate,
                        image = imageUri,
                    )
                }.onSuccess {
                    _successUpload.setValue(UploadStatus.SUCCESS)
                }.onFailure {
                    _successUpload.setValue(UploadStatus.FAIL)
                    setError(it as DataThrowable)
                }
            }
        }
    }

    private fun setError(throwable: DataThrowable) {
        when (throwable) {
            is UniversalThrowable -> _throwable.value = throwable
            is PlaceThrowable -> _throwable.value = throwable
            else -> {}
        }
    }

    companion object {
        val URI_EMPTY = Uri.EMPTY.toString()

        const val ALREADY_EXISTS_NEARBY = 505
        const val ERROR_STORE_PHOTO = 215
        const val ERROR_POST_BODY = 205
    }
}
