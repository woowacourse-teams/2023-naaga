package com.now.naaga.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.RankRepository
import com.now.domain.repository.StatisticsRepository

class MyPageFactory(
    private val rankRepository: RankRepository,
    private val statisticsRepository: StatisticsRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(rankRepository, statisticsRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
