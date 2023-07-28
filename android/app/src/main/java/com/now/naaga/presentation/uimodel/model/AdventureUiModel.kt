package com.now.naaga.presentation.uimodel.model

import android.os.Parcelable
import com.now.domain.model.AdventureStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class AdventureUiModel(
    val id: Long,
    val destination: DestinationUiModel,
    val adventureStatus: AdventureStatus,
) : Parcelable
