package com.now.naaga.data.mapper

import com.now.domain.model.Direction
import com.now.domain.model.Hint
import com.now.naaga.data.remote.dto.third.HintDto

fun HintDto.toDomain(): Hint {
    return Hint(
        id = id,
        coordinate = coordinate.toDomain(),
        direction = Direction.findByName(direction),
    )
}
