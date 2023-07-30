package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AdventureResultViewModel : ViewModel() {

    private val _adventureResultState = MutableLiveData(false)
    val adventureResultState: LiveData<Boolean> = _adventureResultState

    private val _name = MutableLiveData<String>()
    val name: LiveData<String> = _name

    fun checkAdventureResultState(state: Boolean) {
        _adventureResultState.value = state
    }

    companion object {
        const val TEMP_SUCCESS_CODE = true
        const val TEMP_FAIL_CODE = false
    }
}
