package com.now.naaga.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class AdventureStatusDto(
    val id: Long,
    val gameStatus: String,
)
