package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EndedAdventureDto(
    @SerialName("id")
    val id: Long,
    @SerialName("gameStatus")
    val adventureStatus: String,
)
