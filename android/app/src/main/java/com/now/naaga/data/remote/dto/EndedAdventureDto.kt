package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName

data class EndedAdventureDto(
    val id: Long,
    @SerialName("gameStatus")
    val adventureStatus: String,
)
