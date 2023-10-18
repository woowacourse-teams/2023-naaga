package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PreferenceDto(
    @SerialName("id")
    val id: Int,
    @SerialName("playerId")
    val playerId: Int,
    @SerialName("placeId")
    val placeId: Int,
    @SerialName("type")
    val type: String,
)
