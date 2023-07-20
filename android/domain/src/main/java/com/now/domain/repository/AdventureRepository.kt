package com.now.domain.repository

import com.now.domain.model.Adventure
import com.now.domain.model.Coordinate

interface AdventureRepository {
    fun beginAdventure(callback: (Result<Unit>) -> Unit)

    fun getAdventure(adventureId: Long, callback: (Result<Adventure>) -> Unit)

    fun endAdventure(adventureId: Long, coordinate: Coordinate, callback: (Result<Unit>) -> Unit)
}
