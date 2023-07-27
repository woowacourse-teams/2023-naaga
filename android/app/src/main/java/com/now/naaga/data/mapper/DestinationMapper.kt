package com.now.naaga.data.mapper

import com.now.domain.model.Destination
import com.now.naaga.data.remote.dto.DestinationDto

fun DestinationDto.toDomain(): Destination {
    return Destination(
        id = id,
        coordinate = coordinate.toDomain(),
        image = imageUrl
    )
}
