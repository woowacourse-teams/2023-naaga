package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostPlaceDto(
    @SerialName("id")
    val id: Long,
    @SerialName("name")
    val name: String,
    @SerialName("coordinate")
    val coordinate: CoordinateDto,
    @SerialName("imageUrl")
    val imageUrl: String,
    @SerialName("description")
    val description: String,
    @SerialName("registeredPlayerId")
    val registeredPlayerId: Long,
)
