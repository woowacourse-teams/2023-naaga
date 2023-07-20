package com.now.naaga.data.repository

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.AdventureRepository

class MockAdventureRepository : AdventureRepository {
    override fun beginAdventure(callback: (Result<Unit>) -> Unit) {
        TODO("Not yet implemented")
    }

    override fun getAdventure(adventureId: Long, callback: (Result<Adventure>) -> Unit) {
        when (adventureId) {
            1L -> callback(Result.success(woowaAdventure))
            2L -> callback(Result.success(subwayAdventure))
            else -> callback(Result.success(starbucksAdventure))
        }
    }

    override fun endAdventure(
        adventureId: Long,
        coordinate: Coordinate,
        callback: (Result<Unit>) -> Unit,
    ) {
        TODO("Not yet implemented")
    }

    companion object {
        private val woowaBrotherSmallHouse = Coordinate(37.514907, 127.103198) // 루터회관과 51~54m
        private val subway = Coordinate(37.515178, 127.102597) // 루터회관과 34~38m
        private val starbucks = Coordinate(37.515283, 127.099463) // 루터회관까지 310m

        private val woowaDestination = Destination(1, woowaBrotherSmallHouse, "")
        private val subwayDestination = Destination(2, subway, "")
        private val starbucksDestination = Destination(3, starbucks, "")

        private val woowaAdventure = Adventure(
            id = 1,
            destination = woowaDestination,
            adventureStatus = AdventureStatus.IN_PROGRESS,
        )
        private val subwayAdventure = Adventure(
            id = 2,
            destination = subwayDestination,
            adventureStatus = AdventureStatus.IN_PROGRESS,
        )
        private val starbucksAdventure = Adventure(
            id = 3,
            destination = starbucksDestination,
            adventureStatus = AdventureStatus.IN_PROGRESS,
        )
    }
}
