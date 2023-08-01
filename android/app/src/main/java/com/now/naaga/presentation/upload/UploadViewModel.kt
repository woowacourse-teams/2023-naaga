package com.now.naaga.presentation.upload

import android.text.Editable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class UploadViewModel : ViewModel() {
    private var imageUri: String = ""

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    fun setTitle(editTitle: Editable) {
        _title.value = editTitle.toString()
    }

    fun setDescription(editTitle: Editable) {
        _description.value = editTitle.toString()
    }

    fun setUri(uri: String) {
        imageUri = uri
    }
}
