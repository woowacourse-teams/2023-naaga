package com.now.naaga.data.mapper

import com.now.domain.model.Place
import com.now.naaga.data.remote.dto.third.PlaceDto

fun PlaceDto.toDomain(): Place {
    return Place(
        id = id,
        name = name,
        coordinate = coordinate.toDomain(),
        image = imageUrl,
        description = description,
    )
}
