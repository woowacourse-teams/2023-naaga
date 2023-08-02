package com.now.naaga.presentation.rank

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.RankRepository

class RankFactory(private val rankRepository: RankRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RankViewModel::class.java)) {
            return RankViewModel(rankRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
