package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeCountDto(
    @SerialName("placeLikeCount")
    val placeLikeCount: Int,
)
