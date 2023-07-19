package com.now.naaga.data.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Destination
import com.now.domain.repository.DestinationRepository

class MockDestinationRepository : DestinationRepository {
    override fun getDestination(gameId: Long): Destination {
        return when (gameId) {
            1L -> woowaDestination
            2L -> subwayDestination
            else -> starbucksDestination
        }
    }

    override fun getIsArrival(coordinate: Coordinate): Boolean {
        TODO("Not yet implemented")
    }

    companion object {
        private val woowaBrotherSmallHouse = Coordinate(37.514907, 127.103198) // 루터회관과 51~54m
        private val subway = Coordinate(37.515178, 127.102597) // 루터회관과 34~38m
        private val starbucks = Coordinate(37.515283, 127.099463) // 루터회관까지 310m

        private val woowaDestination = Destination(1, woowaBrotherSmallHouse, "")
        private val subwayDestination = Destination(2, subway, "")
        private val starbucksDestination = Destination(3, starbucks, "")
    }
}
