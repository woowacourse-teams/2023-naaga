package com.now.naaga.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FailureDto(
    @SerialName("code")
    val code: Int,
    @SerialName("message")
    val message: String,
)
