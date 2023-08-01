package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureResult
import com.now.domain.repository.AdventureRepository2
import com.now.domain.repository.RankRepository

class AdventureResultViewModel(
    private val adventureRepository: AdventureRepository2,
    private val rankRepository: RankRepository,
) : ViewModel() {

    private val _adventureResult = MutableLiveData<AdventureResult>()
    val adventureResult: LiveData<AdventureResult> = _adventureResult

    private val _myRank = MutableLiveData<Int>()
    val myRank: LiveData<Int> = _myRank

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    companion object {
        private const val TEMP_MOCK_GAME_ID = 1L
    }
}
