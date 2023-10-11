package com.now.domain.model.letter

import com.now.domain.model.type.LogType

data class InGameLetter(
    val id: Long,
    val gameId: Long,
    val letterId: Long,
    val logType: LogType,
)
