package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class FailureDto(
    val code: Int,
    val message: String,
)
