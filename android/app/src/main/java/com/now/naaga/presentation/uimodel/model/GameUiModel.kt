package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import com.now.domain.model.AdventureStatus
import kotlinx.parcelize.Parcelize
import java.time.LocalDateTime

@Parcelize
data class GameUiModel(
    val id: Long,
    val createdAt: LocalDateTime,
    val startCoordinate: CoordinateUiModel,
    val destination: PlaceUiModel,
    val player: PlayerUiModel,
    val adventureStatus: AdventureStatus,
    val remainingTryCount: Int,
    val hints: List<HintUiModel>,
) : Parcelable
