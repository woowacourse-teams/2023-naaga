package com.now.naaga.presentation.adventurehistory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.now.domain.model.AdventureResult
import com.now.domain.model.AdventureResultType.FAIL
import com.now.domain.model.AdventureResultType.SUCCESS
import com.now.domain.model.Coordinate
import com.now.domain.model.Place
import java.time.LocalDateTime
import java.time.LocalTime

class AdventureHistoryViewModel : ViewModel() {
    private val _places = MutableLiveData<List<AdventureResult>>()
    val places: LiveData<List<AdventureResult>> = _places

    fun fetchHistories() {
        _places.value = MOCK_ADVENTURE_HISTORY_DATA
    }

    companion object {
        val MOCK_ADVENTURE_HISTORY_DATA = listOf(
            AdventureResult(
                id = 1,
                gameId = 1,
                destination = Place(
                    id = 1,
                    name = "선정릉 돌담길",
                    coordinate = Coordinate(35.3094, 127.21389),
                    image = "",
                    description = "test",
                ),
                resultType = SUCCESS,
                score = 130,
                playTime = LocalTime.of(13, 30, 0),
                distance = 300,
                hintUses = 3,
                tryCount = 2,
                raisedRank = 2,
                beginTime = LocalDateTime.of(2023, 8, 1, 13, 30, 0),
                endTime = LocalDateTime.of(2023, 8, 1, 14, 0, 0),
            ),
            AdventureResult(
                id = 1,
                gameId = 1,
                destination = Place(
                    id = 1,
                    name = "선정릉 돌담길",
                    coordinate = Coordinate(35.3094, 127.21389),
                    image = "",
                    description = "test",
                ),
                resultType = SUCCESS,
                score = 130,
                playTime = LocalTime.of(13, 30, 0),
                distance = 300,
                hintUses = 3,
                tryCount = 2,
                raisedRank = 2,
                beginTime = LocalDateTime.of(2023, 8, 1, 13, 30, 0),
                endTime = LocalDateTime.of(2023, 8, 1, 14, 0, 0),
            ),
            AdventureResult(
                id = 1,
                gameId = 1,
                destination = Place(
                    id = 1,
                    name = "선정릉 돌담길",
                    coordinate = Coordinate(35.3094, 127.21389),
                    image = "",
                    description = "test",
                ),
                resultType = FAIL,
                score = 130,
                playTime = LocalTime.of(13, 30, 0),
                distance = 300,
                hintUses = 3,
                tryCount = 2,
                raisedRank = 2,
                beginTime = LocalDateTime.of(2023, 8, 1, 13, 30, 0),
                endTime = LocalDateTime.of(2023, 8, 1, 14, 0, 0),
            ),
        )
    }
}
