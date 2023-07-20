package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EndedAdventureDto(
    val id: Long,
    @SerialName("gameStatus")
    val adventureStatus: String,
)
