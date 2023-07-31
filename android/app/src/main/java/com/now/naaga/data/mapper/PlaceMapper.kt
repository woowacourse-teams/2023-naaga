package com.now.naaga.data.mapper

import com.now.domain.model.Place
import com.now.naaga.data.remote.dto.third.PlaceDto

fun Place.toDto(): PlaceDto {
    return PlaceDto(
        id = id,
        name = name,
        coordinate = coordinate.toDto(),
        imageUrl = image,
        description = description,
    )
}

fun PlaceDto.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        coordinate = coordinate.toDomain(),
        image = imageUrl,
        description = description,
    )
}
