package com.now.naaga.data.remote.dto.post

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostLetterLogDto(
    @SerialName("gameId")
    val gameId: Long,
    @SerialName("letterId")
    val letterId: Long,
    @SerialName("logType")
    val logType: String,
)
