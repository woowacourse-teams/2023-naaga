package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class PlaceUiModel(
    val id: Long,
    val name: String,
    val coordinate: CoordinateUiModel,
    val image: String,
    val description: String,
) : Parcelable
