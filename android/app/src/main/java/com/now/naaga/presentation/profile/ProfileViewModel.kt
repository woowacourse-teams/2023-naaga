package com.now.naaga.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.now.domain.repository.ProfileRepository
import com.now.naaga.data.throwable.DataThrowable
import com.now.naaga.data.throwable.DataThrowable.NetworkThrowable
import com.now.naaga.data.throwable.DataThrowable.PlayerThrowable
import com.now.naaga.data.throwable.DataThrowable.UniversalThrowable
import com.now.naaga.util.singleliveevent.MutableSingleLiveData
import com.now.naaga.util.singleliveevent.SingleLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    val nickname = MutableLiveData<String>()

    private val _modifyStatus = MutableSingleLiveData<Boolean>()
    val modifyStatus: SingleLiveData<Boolean> = _modifyStatus

    private val _throwable = MutableLiveData<DataThrowable>()
    val throwable: LiveData<DataThrowable> = _throwable

    fun modifyNickname() {
        viewModelScope.launch {
            runCatching {
                profileRepository.modifyNickname(nickname.value.toString())
            }.onSuccess {
                _modifyStatus.setValue(true)
            }.onFailure {
                setThrowable(it)
            }
        }
    }

    fun isFormValid(): Boolean {
        return nickname.value != null
    }

    private fun setThrowable(throwable: Throwable) {
        when (throwable) {
            is IOException -> _throwable.value = NetworkThrowable()
            is UniversalThrowable -> _throwable.value = throwable
            is PlayerThrowable -> _throwable.value = throwable
        }
    }
}
