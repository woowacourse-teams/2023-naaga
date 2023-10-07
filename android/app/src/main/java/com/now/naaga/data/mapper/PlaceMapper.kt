package com.now.naaga.data.mapper

import com.now.domain.model.Place
import com.now.naaga.data.remote.dto.PlaceDto
import com.now.naaga.data.remote.dto.PostPlaceDto

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

fun PostPlaceDto.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        coordinate = coordinate.toDomain(),
        image = imageUrl,
        description = description,
    )
}
