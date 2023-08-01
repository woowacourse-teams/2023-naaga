package com.now.naaga.presentation.mypage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.repository.RankRepository

class MyPageFactory(
    private val rankRepository: RankRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MyPageViewModel::class.java)) {
            return MyPageViewModel(rankRepository) as T
        } else {
            throw IllegalArgumentException()
        }
    }
}
