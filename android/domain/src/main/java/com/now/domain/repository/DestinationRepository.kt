package com.now.domain.repository

import com.now.domain.model.Coordinate
import com.now.domain.model.Destination

interface DestinationRepository {
    fun getDestination(gameId: Long): Destination

    fun getIsArrival(coordinate: Coordinate): Boolean
}
