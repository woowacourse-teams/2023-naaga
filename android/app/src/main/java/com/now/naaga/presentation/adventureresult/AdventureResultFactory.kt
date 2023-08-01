package com.now.naaga.presentation.adventureresult

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.AdventureRepository2
import com.now.domain.repository.RankRepository

class AdventureResultFactory(
    private val adventureRepository: AdventureRepository2,
    private val rankRepository: RankRepository,
) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AdventureResultViewModel::class.java)) {
            return AdventureResultViewModel(adventureRepository, rankRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
