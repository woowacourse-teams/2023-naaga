package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AdventureDto(
    val id: Long,
    @SerialName("place")
    val destinationDto: DestinationDto,
    @SerialName("gameStatus")
    val adventureStatus: String,
)
