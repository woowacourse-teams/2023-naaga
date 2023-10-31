package com.now.naaga.data.mapper

import com.now.domain.model.Coordinate
import com.now.naaga.data.remote.dto.CoordinateDto

fun CoordinateDto.toDomain(): Coordinate {
    return Coordinate(latitude, longitude)
}

fun Coordinate.toDto(): CoordinateDto {
    return CoordinateDto(latitude, longitude)
}
