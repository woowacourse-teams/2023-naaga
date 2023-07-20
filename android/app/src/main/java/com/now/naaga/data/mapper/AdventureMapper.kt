package com.now.naaga.data.mapper

import com.now.domain.model.Adventure
import com.now.domain.model.AdventureStatus
import com.now.naaga.data.remote.dto.AdventureDto
import com.now.naaga.data.remote.dto.EndedAdventureDto

fun AdventureDto.toDomain(): Adventure {
    return Adventure(
        id = id,
        destination = destinationDto.toDomain(),
        adventureStatus = AdventureStatus.getStatus(adventureStatus),
    )
}

fun EndedAdventureDto.toDomain(): AdventureStatus {
    return AdventureStatus.getStatus(adventureStatus)
}
