package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import com.now.domain.model.Direction
import kotlinx.parcelize.Parcelize

@Parcelize
data class HintUiModel(
    val id: Long,
    val coordinate: CoordinateUiModel,
    val direction: Direction,
) : Parcelable
