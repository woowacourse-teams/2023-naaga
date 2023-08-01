package com.now.naaga.presentation.onadventure

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.now.domain.model.Game
import com.now.domain.repository.AdventureRepository2
import com.now.naaga.data.repository.ThirdDemoAdventureRepository

class OnAdventureViewModel2(private val adventureRepository2: AdventureRepository2) : ViewModel() {
    private val _adventure = MutableLiveData<Game>()
    val adventure: LiveData<Game> = _adventure

    fun setInProgressAdventure(game: Game) {
    }

    companion object {
        val Factory = ViewModelFactory(ThirdDemoAdventureRepository())

        class ViewModelFactory(private val adventureRepository2: AdventureRepository2) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OnAdventureViewModel(adventureRepository2) as T
            }
        }
    }
}
