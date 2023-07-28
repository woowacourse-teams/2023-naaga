package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class DestinationUiModel(
    val id: Long,
    val coordinate: CoordinateUiModel,
    val image: String,
) : Parcelable
