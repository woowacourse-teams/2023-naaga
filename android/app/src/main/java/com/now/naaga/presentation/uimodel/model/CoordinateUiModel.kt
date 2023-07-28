package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CoordinateUiModel(
    val latitude: Double,
    val longitude: Double,
) : Parcelable
