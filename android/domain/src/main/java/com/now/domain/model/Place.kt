package com.now.domain.model

data class Place(
    val id: Long,
    val name: String,
    val coordinate: Coordinate,
    val image: String,
    val description: String,
)
