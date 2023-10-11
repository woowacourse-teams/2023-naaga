package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LetterLogDto(
    @SerialName("id")
    val id: Long,
    @SerialName("gameId")
    val gameId: Long,
    @SerialName("letterId")
    val letterId: Long,
    @SerialName("logType")
    val logType: String,
)
